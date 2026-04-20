package com.netsentinel.controllers;

import com.netsentinel.models.Alert;
import com.netsentinel.repositories.AlertRepository;
import com.netsentinel.repositories.LogEventRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "🖥️ Dashboard", description = "Panel visual de eventos y alertas")
public class DashboardController {

    private final AlertRepository alertRepository;
    private final LogEventRepository logEventRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Alert> alerts = alertRepository.findAll();

        long totalLogs     = logEventRepository.count();
        long totalAlerts   = alerts.size();
        long newAlerts     = alerts.stream().filter(a -> a.getStatus().name().equals("NEW")).count();
        long criticalAlerts = alerts.stream()
                .filter(a -> a.getSeverity().name().equals("CRITICAL") || a.getSeverity().name().equals("HIGH"))
                .count();

        model.addAttribute("alerts", alerts);
        model.addAttribute("totalLogs", totalLogs);
        model.addAttribute("totalAlerts", totalAlerts);
        model.addAttribute("newAlerts", newAlerts);
        model.addAttribute("criticalAlerts", criticalAlerts);

        return "dashboard";
    }
}
