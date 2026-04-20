package com.netsentinel.dto;

import com.netsentinel.models.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogIngestionRequest {

    @NotBlank
    private String sourceIp;

    @NotBlank
    private String eventType;

    @NotNull
    private Severity severity;

    private String payload;
    private String applicationName;
}
