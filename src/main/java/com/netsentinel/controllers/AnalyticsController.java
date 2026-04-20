package com.netsentinel.controllers;

import com.netsentinel.dto.HourlyCountDto;
import com.netsentinel.dto.IpCountDto;
import com.netsentinel.dto.SeverityCountDto;
import com.netsentinel.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "📊 Analytics", description = "Estadísticas y métricas agregadas del motor de eventos")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/severity-distribution")
    @Operation(summary = "Distribución de eventos por severidad",
               description = "Devuelve el conteo de LogEvents agrupados por nivel de severidad (LOW/MEDIUM/HIGH/CRITICAL).")
    public ResponseEntity<List<SeverityCountDto>> severityDistribution() {
        return ResponseEntity.ok(analyticsService.getSeverityDistribution());
    }

    @GetMapping("/top-ips")
    @Operation(summary = "Top 5 IPs con más eventos",
               description = "Devuelve las 5 IPs de origen con mayor volumen de eventos registrado.")
    public ResponseEntity<List<IpCountDto>> topIps() {
        return ResponseEntity.ok(analyticsService.getTopIps(5));
    }

    @GetMapping("/hourly-volume")
    @Operation(summary = "Volumen de logs por hora (últimas 24h)",
               description = "Devuelve el número de eventos por hora en las últimas 24 horas. Horas sin actividad se incluyen con count=0.")
    public ResponseEntity<List<HourlyCountDto>> hourlyVolume() {
        return ResponseEntity.ok(analyticsService.getHourlyVolume());
    }

    @GetMapping("/alert-type-distribution")
    @Operation(summary = "Distribución de alertas por tipo de regla",
               description = "Devuelve el conteo de alertas agrupadas por ruleName (Brute Force, SQLi, DoS, Endpoint Scan).")
    public ResponseEntity<List<SeverityCountDto>> alertTypeDistribution() {
        return ResponseEntity.ok(analyticsService.getAlertTypeDistribution());
    }
}
