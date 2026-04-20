package com.netsentinel.controllers;

import com.netsentinel.dto.LogIngestionRequest;
import com.netsentinel.services.LogIngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogIngestionController {

    private final LogIngestionService logIngestionService;

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingestLog(@Valid @RequestBody LogIngestionRequest request) {
        logIngestionService.ingestLog(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // 202 Accepted, ya que se procesa asíncronamente el análisis
    }
}
