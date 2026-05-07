package com.vforce360.mocks;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationAssessmentPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of the Port.
 * Simulates a database call without external dependencies.
 */
public class MockModernizationAssessmentRepository implements ModernizationAssessmentPort {

    @Override
    public ModernizationAssessmentReport findByProjectId(String projectId) {
        // Simulating a brownfield project data structure
        // This structure matches the 'Raw JSON' described in the defect.
        Map<String, Object> rawContent = new HashMap<>();
        rawContent.put("summary", "Legacy mainframe system requires modernization.");
        rawContent.put("risks", Map.of(
            "technical", "High reliance on COBOL/PL-I",
            "operational", "Aging hardware"
        ));
        rawContent.put("recommendation", "Refactor to Spring Boot microservices.");

        return new ModernizationAssessmentReport(
            projectId,
            "Modernization Assessment: Core Banking",
            rawContent,
            "GENERATED"
        );
    }
}
