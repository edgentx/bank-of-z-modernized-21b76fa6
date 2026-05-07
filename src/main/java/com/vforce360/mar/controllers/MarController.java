package com.vforce360.mar.controllers;

import com.vforce360.ports.MarReportPort;
import com.vforce360.models.MarReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final MarReportPort marReportPort;

    public MarController(MarReportPort marReportPort) {
        this.marReportPort = marReportPort;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarReport> getMarReport(@PathVariable UUID id) {
        return ResponseEntity.ok(marReportPort.findById(id));
    }
}
