package com.vforce360.mar.mocks;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import com.vforce360.mar.ports.MarStoragePort;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Profile("test")
@Primary
public class MockMarStorageAdapter implements MarStoragePort {

    @Override
    public Optional<ModernizationAssessmentReport> findById(UUID id) {
        // Returning a specific fixture that simulates the "Raw JSON" defect behavior
        ModernizationAssessmentReport report = new ModernizationAssessmentReport();
        report.setId(id);
        // This raw JSON string is the defect data.
        report.setRawJsonContent("{\"heading\": \"Assessment\", \"content\": \"Legacy code found\"}");
        return Optional.of(report);
    }
}
