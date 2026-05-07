package com.vforce360.mocks;

import com.vforce360.mar.model.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;

import java.util.Optional;

/**
 * Mock Adapter for the Modernization Report Repository.
 * Returns predictable data for testing without hitting MongoDB.
 */
public class MockReportRepository implements ModernizationReportPort {

    private String fixedRawContent = "";
    private boolean shouldReturnEmpty = false;

    public void setFixedRawContent(String content) {
        this.fixedRawContent = content;
    }

    public void setShouldReturnEmpty(boolean shouldReturnEmpty) {
        this.shouldReturnEmpty = shouldReturnEmpty;
    }

    @Override
    public Optional<ModernizationAssessmentReport> findByProjectId(String projectId) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        // Simulating a Brownfield project with MAR generated
        ModernizationAssessmentReport report = new ModernizationAssessmentReport(projectId, fixedRawContent);
        return Optional.of(report);
    }
}