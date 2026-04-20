package com.netsentinel.services;

import com.netsentinel.dto.HourlyCountDto;
import com.netsentinel.dto.IpCountDto;
import com.netsentinel.dto.SeverityCountDto;
import com.netsentinel.repositories.AlertRepository;
import com.netsentinel.repositories.LogEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final LogEventRepository logEventRepository;
    private final AlertRepository    alertRepository;

    /** Distribución de LogEvents por severidad */
    public List<SeverityCountDto> getSeverityDistribution() {
        return logEventRepository.countGroupedBySeverity().stream()
                .map(row -> new SeverityCountDto(row[0].toString(), ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    /** Top N IPs por volumen total de eventos */
    public List<IpCountDto> getTopIps(int limit) {
        return logEventRepository.findTopIps(PageRequest.of(0, limit)).stream()
                .map(row -> new IpCountDto(row[0].toString(), ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Volumen de logs por hora — últimas 24 horas.
     * Rellena con 0 las horas sin actividad para gráfica continua.
     */
    public List<HourlyCountDto> getHourlyVolume() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Object[]> rows = logEventRepository.countByHourSince(since);

        Map<Integer, Long> hMap = rows.stream().collect(Collectors.toMap(
                r -> ((Number) r[0]).intValue(),
                r -> ((Number) r[1]).longValue()
        ));

        int currentHour = LocalDateTime.now().getHour();
        List<HourlyCountDto> result = new ArrayList<>();
        for (int i = 23; i >= 0; i--) {
            int hour = (currentHour - i + 24) % 24;
            result.add(new HourlyCountDto(hour, hMap.getOrDefault(hour, 0L)));
        }
        return result;
    }

    /** Distribución de Alertas por tipo de regla (ruleName) */
    public List<SeverityCountDto> getAlertTypeDistribution() {
        return alertRepository.countGroupedByRuleName().stream()
                .map(row -> new SeverityCountDto(
                        formatRuleName(row[0].toString()),
                        ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    /** Convierte BRUTE_FORCE_LOGIN_ATTEMPT → Brute Force */
    private String formatRuleName(String raw) {
        return switch (raw) {
            case "BRUTE_FORCE_LOGIN_ATTEMPT"   -> "Brute Force";
            case "SQL_INJECTION_ATTEMPT"       -> "SQL Injection";
            case "DOS_ATTACK_DETECTED"         -> "DoS Attack";
            case "ENDPOINT_SCANNING_DETECTED"  -> "Endpoint Scan";
            default -> raw;
        };
    }
}
