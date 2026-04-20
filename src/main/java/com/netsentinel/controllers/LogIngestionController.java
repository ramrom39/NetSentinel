package com.netsentinel.controllers;

import com.netsentinel.dto.LogIngestionRequest;
import com.netsentinel.services.LogIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "📡 Ingesta de Logs", description = "Endpoint para inyectar eventos de seguridad al motor de análisis")
@SecurityRequirement(name = "BearerAuth")
public class LogIngestionController {

    private final LogIngestionService logIngestionService;

    @PostMapping("/ingest")
    @Operation(
        summary = "Ingestar un evento de seguridad",
        description = """
            Recibe un evento de seguridad y lo persiste de forma síncrona. \
            El análisis del motor de reglas ocurre de forma **asíncrona**.
            
            **Prueba de brute-force**: Envía 5 veces este request con `eventType: LOGIN_FAILED` \
            y la misma `sourceIp` para ver cómo se genera una `Alert` automáticamente en la BD.
            """,
        responses = {
            @ApiResponse(responseCode = "202", description = "Evento aceptado y en proceso de análisis"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT no proporcionado o inválido")
        }
    )
    public ResponseEntity<Void> ingestLog(@Valid @RequestBody LogIngestionRequest request) {
        logIngestionService.ingestLog(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
