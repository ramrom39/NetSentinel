package com.netsentinel.dto;

import com.netsentinel.models.Severity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Payload para ingestar un evento de seguridad")
public class LogIngestionRequest {

    @NotBlank
    @Schema(
        description = "IP de origen del evento",
        example = "192.168.1.100"
    )
    private String sourceIp;

    @NotBlank
    @Schema(
        description = "Tipo de evento. Para disparar el motor de alertas usa LOGIN_FAILED.",
        example = "LOGIN_FAILED",
        allowableValues = {"LOGIN_FAILED", "PORT_SCAN", "ACCESS_DENIED", "MALWARE_DETECTED"}
    )
    private String eventType;

    @NotNull
    @Schema(
        description = "Severidad del evento",
        example = "HIGH"
    )
    private Severity severity;

    @Schema(
        description = "Detalle libre del evento en texto o JSON",
        example = "Intento de login fallido para el usuario 'root' desde cliente externo"
    )
    private String payload;

    @Schema(
        description = "Nombre de la aplicación de origen",
        example = "AuthService"
    )
    private String applicationName;
}
