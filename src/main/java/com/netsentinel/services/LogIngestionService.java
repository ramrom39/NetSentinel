package com.netsentinel.services;

import com.netsentinel.dto.LogIngestionRequest;
import com.netsentinel.models.LogEvent;
import com.netsentinel.models.Severity;
import com.netsentinel.repositories.LogEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogIngestionService {

    private final LogEventRepository logEventRepository;
    private final RuleEngineService ruleEngineService;

    public void ingestLog(LogIngestionRequest request) {
        log.info("Ingesting new log event: {} from {}", request.getEventType(), request.getSourceIp());

        // ── Enriquecimiento de Severidad por Frecuencia ───────────────────
        Severity enrichedSeverity = enrichSeverity(request);
        if (enrichedSeverity != request.getSeverity()) {
            log.warn("[ENRICHMENT] IP {} escalada de {} a {} por alta frecuencia de eventos",
                    request.getSourceIp(), request.getSeverity(), enrichedSeverity);
        }

        LogEvent logEvent = LogEvent.builder()
                .timestamp(LocalDateTime.now())
                .sourceIp(request.getSourceIp())
                .eventType(request.getEventType())
                .severity(enrichedSeverity)           // ← Severidad enriquecida
                .payload(request.getPayload())
                .applicationName(request.getApplicationName())
                .build();

        // 1. Guardar síncronamente (el dato no se pierde)
        logEventRepository.save(logEvent);
        log.debug("[INGEST] LogEvent #{} guardado. Severidad final: {}", logEvent.getId(), enrichedSeverity);

        // 2. Analizar asíncronamente (no bloquea la respuesta HTTP)
        CompletableFuture.runAsync(() -> {
            try {
                ruleEngineService.analyzeLogEvent(logEvent);
            } catch (Exception e) {
                log.error("[INGEST] Error al analizar el evento en el motor de reglas", e);
            }
        });
    }

    /**
     * Enriquece la severidad del evento basándose en la frecuencia acumulada
     * de la IP de origen en los últimos 5 minutos.
     *
     * Umbrales:
     *   ≥ 30 eventos → CRITICAL (ataque masivo)
     *   ≥ 15 eventos → HIGH (actividad sospechosa intensa)
     *   ≥  6 eventos → MEDIUM (patrón anómalo)
     *   <  6 eventos → mantiene la severidad original del cliente
     */
    private Severity enrichSeverity(LogIngestionRequest request) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        long recentCount = logEventRepository.countAllBySourceIpSince(
                request.getSourceIp(), fiveMinutesAgo);

        if (recentCount >= 30) return Severity.CRITICAL;
        if (recentCount >= 15) return Severity.HIGH;
        if (recentCount >= 6)  return Severity.MEDIUM;
        return request.getSeverity();
    }
}
