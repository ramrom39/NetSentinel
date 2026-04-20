package com.netsentinel.services;

import com.netsentinel.dto.LogIngestionRequest;
import com.netsentinel.models.LogEvent;
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

        LogEvent logEvent = LogEvent.builder()
                .timestamp(LocalDateTime.now())
                .sourceIp(request.getSourceIp())
                .eventType(request.getEventType())
                .severity(request.getSeverity())
                .payload(request.getPayload())
                .applicationName(request.getApplicationName())
                .build();

        // 1. Guardar rápido (Síncrono para no perder el dato en la respuesta)
        logEventRepository.save(logEvent);

        // 2. Analizar asíncronamente
        CompletableFuture.runAsync(() -> {
            try {
                ruleEngineService.analyzeLogEvent(logEvent);
            } catch (Exception e) {
                log.error("Error analyzing log event", e);
            }
        });
    }
}
