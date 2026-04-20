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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final LogEventRepository logEventRepository;
    private final AlertRepository alertRepository;

    public void analyzeLogEvent(LogEvent event) {
        log.debug("Analyzing event: {} from IP: {}", event.getEventType(), event.getSourceIp());

        // Basic Rule: >5 failed login attempts from the same IP within 1 minute
        if ("LOGIN_FAILED".equalsIgnoreCase(event.getEventType())) {
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            
            long failedCount = logEventRepository.countBySourceIpAndEventTypeSince(
                    event.getSourceIp(), "LOGIN_FAILED", oneMinuteAgo);

            log.debug("Found {} LOGIN_FAILED events for IP {} in the last minute.", failedCount, event.getSourceIp());

            if (failedCount >= 5) {
                log.warn("Rule triggered! Generating alert for IP: {} due to excessive failed logins.", event.getSourceIp());
                
                Alert alert = Alert.builder()
                        .ruleName("BRUTE_FORCE_LOGIN_ATTEMPT")
                        .description("Detected " + failedCount + " failed login attempts from IP " + event.getSourceIp() + " within 1 minute.")
                        .sourceIp(event.getSourceIp())
                        .severity(Severity.HIGH)
                        .status(AlertStatus.NEW)
                        .timestamp(LocalDateTime.now())
                        .build();
                        
                alertRepository.save(alert);
            }
        }
    }
}
