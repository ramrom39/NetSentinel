package com.netsentinel.controllers;

import com.netsentinel.models.Alert;
import com.netsentinel.repositories.AlertRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "🚨 Alertas", description = "Consulta de alertas generadas por el motor de reglas")
@SecurityRequirement(name = "BearerAuth")
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping
    @Operation(
        summary = "Listar todas las alertas",
        description = "Devuelve todas las alertas generadas por el motor de reglas. También usada por el Dashboard para refrescar."
    )
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
}
