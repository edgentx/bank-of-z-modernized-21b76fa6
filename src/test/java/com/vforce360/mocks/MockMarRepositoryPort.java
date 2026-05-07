package com.vforce360.mocks;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarRepositoryPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of MarRepositoryPort for testing.
 * Simulates a database without needing a real DB connection.
 */
public class MockMarRepositoryPort implements MarRepositoryPort {

    private final Map<String, ModernizationAssessmentReport> database = new HashMap<>();

    @Override
    public ModernizationAssessmentReport findByProjectId(String projectId) {
        // Simulate database behavior
        if (database.containsKey(projectId)) {
            return database.get(projectId);
        }
        return null;
    }

    // Helper method to setup test data
    public void saveReport(String projectId, ModernizationAssessmentReport report) {
        database.put(projectId, report);
    }
}