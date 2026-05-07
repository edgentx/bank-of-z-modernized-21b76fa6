package com.vforce360.mocks;

import com.vforce360.ports.ModernizationReportPort;
import com.vforce360.mar.models.ModernizationAssessmentReport;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of ModernizationReportPort.
 * Used in tests to simulate database responses without connecting to MongoDB.
 */
public class MockModernizationReportAdapter implements ModernizationReportPort {

    private final Map<String, ModernizationAssessmentReport> database = new HashMap<>();

    public MockModernizationReportAdapter() {
        // Initialize with a sample report for the specific project ID in the story
        // Simulating the Defect: raw content might look like JSON or MD
        String sampleMarkdown = "# Modernization Assessment Report\n\n" +
                "## Executive Summary\n" +
                "* Risk Level: High\n" +
                "* Effort: 6 months\n";

        ModernizationAssessmentReport report = new ModernizationAssessmentReport(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", 
            "Legacy Banking System Migration", 
            sampleMarkdown, 
            "PENDING_REVIEW"
        );
        database.put("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", report);
    }

    @Override
    public ModernizationAssessmentReport getReport(String projectId) {
        if (!database.containsKey(projectId)) {
            throw new RuntimeException("Project not found: " + projectId);
        }
        return database.get(projectId);
    }

    // Helper to set specific test data
    public void setReport(String projectId, ModernizationAssessmentReport report) {
        database.put(projectId, report);
    }
}
