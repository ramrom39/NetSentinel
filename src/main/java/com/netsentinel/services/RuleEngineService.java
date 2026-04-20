package com.netsentinel.services;

import com.netsentinel.models.Alert;
import com.netsentinel.models.AlertStatus;
import com.netsentinel.models.LogEvent;
import com.netsentinel.models.Severity;
import com.netsentinel.repositories.AlertRepository;
import com.netsentinel.repositories.LogEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final LogEventRepository logEventRepository;
    private final AlertRepository    alertRepository;

    // ── Palabras clave de SQL Injection ──────────────────────
    private static final Set<String> SQLI_KEYWORDS = Set.of(
            "SELECT", "DROP", "UNION", "INSERT", "DELETE", "UPDATE",
            "EXEC", "EXECUTE", "SCRIPT", "--", "OR 1=1", "' OR", "\"OR"
    );

    // ── Umbral DoS: más de 20 eventos en 30 segundos ─────────
    private static final int DOS_THRESHOLD_COUNT   = 20;
    private static final int DOS_WINDOW_SECONDS    = 30;

    // ── Umbral Endpoint Scanning: más de 8 veces en 1 min ───
    private static final int SCAN_THRESHOLD_COUNT  = 8;

    // ── Eventos consideros "404 / endpoint scan" ─────────────
    private static final Set<String> SCAN_EVENT_TYPES = Set.of(
            "NOT_FOUND", "HTTP_404", "ENDPOINT_NOT_FOUND", "404"
    );

    public void analyzeLogEvent(LogEvent event) {
        log.debug("[RULES] Analyzing event: {} from {}", event.getEventType(), event.getSourceIp());

        checkBruteForce(event);
        checkSqlInjection(event);
        checkDoS(event);
        checkEndpointScanning(event);
    }

    /* ══════════════════════════════════════════════════════════
       REGLA 1 · Brute Force Login
       ≥ 5 LOGIN_FAILED de la misma IP en 1 minuto
    ══════════════════════════════════════════════════════════ */
    private void checkBruteForce(LogEvent event) {
        if (!"LOGIN_FAILED".equalsIgnoreCase(event.getEventType())) return;

        LocalDateTime since = LocalDateTime.now().minusMinutes(1);
        long count = logEventRepository.countBySourceIpAndEventTypeSince(
                event.getSourceIp(), "LOGIN_FAILED", since);

        log.debug("[BRUTE_FORCE] IP {} → {} intentos en el último minuto", event.getSourceIp(), count);

        if (count >= 5) {
            saveAlert(
                    "BRUTE_FORCE_LOGIN_ATTEMPT",
                    "Detectados " + count + " intentos de login fallidos desde " + event.getSourceIp() + " en 1 minuto.",
                    event.getSourceIp(),
                    Severity.HIGH
            );
        }
    }

    /* ══════════════════════════════════════════════════════════
       REGLA 2 · SQL Injection
       Payload contiene palabras clave de SQL
    ══════════════════════════════════════════════════════════ */
    private void checkSqlInjection(LogEvent event) {
        String payload = event.getPayload();
        if (!StringUtils.hasText(payload)) return;

        String upperPayload = payload.toUpperCase();
        String matchedKeyword = SQLI_KEYWORDS.stream()
                .filter(kw -> upperPayload.contains(kw.toUpperCase()))
                .findFirst()
                .orElse(null);

        if (matchedKeyword != null) {
            log.warn("[SQLI] Payload sospechoso de IP {} — keyword: '{}'", event.getSourceIp(), matchedKeyword);
            saveAlert(
                    "SQL_INJECTION_ATTEMPT",
                    "Keyword SQL sospechosa detectada en payload: '" + matchedKeyword +
                    "' | Origen: " + event.getSourceIp() +
                    " | App: " + event.getApplicationName(),
                    event.getSourceIp(),
                    Severity.CRITICAL
            );
        }
    }

    /* ══════════════════════════════════════════════════════════
       REGLA 3 · Denegación de Servicio (DoS)
       > 20 eventos de la misma IP en 30 segundos
    ══════════════════════════════════════════════════════════ */
    private void checkDoS(LogEvent event) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(DOS_WINDOW_SECONDS);
        long count = logEventRepository.countAllBySourceIpSince(event.getSourceIp(), since);

        log.debug("[DoS] IP {} → {} eventos en los últimos {}s", event.getSourceIp(), count, DOS_WINDOW_SECONDS);

        if (count > DOS_THRESHOLD_COUNT) {
            log.warn("[DoS] Umbral superado para IP {} ({} eventos en {}s)", event.getSourceIp(), count, DOS_WINDOW_SECONDS);
            saveAlert(
                    "DOS_ATTACK_DETECTED",
                    "IP " + event.getSourceIp() + " envió " + count +
                    " eventos en " + DOS_WINDOW_SECONDS + " segundos. Posible ataque DoS.",
                    event.getSourceIp(),
                    Severity.CRITICAL
            );
        }
    }

    /* ══════════════════════════════════════════════════════════
       REGLA 4 · Endpoint Scanning / 404 Repetido
       ≥ 8 eventos 404/NOT_FOUND desde la misma IP en 1 minuto
    ══════════════════════════════════════════════════════════ */
    private void checkEndpointScanning(LogEvent event) {
        if (!SCAN_EVENT_TYPES.contains(event.getEventType().toUpperCase())) return;

        LocalDateTime since = LocalDateTime.now().minusMinutes(1);
        long count = 0;
        for (String evType : SCAN_EVENT_TYPES) {
            count += logEventRepository.countBySourceIpAndEventTypeSince(
                    event.getSourceIp(), evType, since);
        }

        log.debug("[SCAN] IP {} → {} peticiones 404 en el último minuto", event.getSourceIp(), count);

        if (count >= SCAN_THRESHOLD_COUNT) {
            log.warn("[SCAN] Posible escaneo de endpoints desde IP {}", event.getSourceIp());
            saveAlert(
                    "ENDPOINT_SCANNING_DETECTED",
                    "IP " + event.getSourceIp() + " realizó " + count +
                    " peticiones a endpoints no encontrados (404) en 1 minuto.",
                    event.getSourceIp(),
                    Severity.MEDIUM
            );
        }
    }

    /* ── Helper: guardar alerta deduplicada ─────────────────── */
    private void saveAlert(String ruleName, String description, String sourceIp, Severity severity) {
        Alert alert = Alert.builder()
                .ruleName(ruleName)
                .description(description)
                .sourceIp(sourceIp)
                .severity(severity)
                .status(AlertStatus.NEW)
                .timestamp(LocalDateTime.now())
                .build();
        alertRepository.save(alert);
        log.info("[ALERT ✓] {} — IP: {} — Severidad: {}", ruleName, sourceIp, severity);
    }
}
