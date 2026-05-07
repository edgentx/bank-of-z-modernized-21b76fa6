package com.vforce360.mar.adapters;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import com.vforce360.mar.ports.MarStoragePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final MarStoragePort storagePort;

    public MarController(MarStoragePort storagePort) {
        this.storagePort = storagePort;
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<String> getMarReview(@PathVariable UUID id) {
        // Placeholder implementation - Returns the raw defect behavior
        return ResponseEntity.ok(storagePort.findById(id)
                .map(ModernizationAssessmentReport::getRawJsonContent)
                .orElse("{}"));
    }
}
