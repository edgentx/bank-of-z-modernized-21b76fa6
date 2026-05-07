package com.vforce360.mocks;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import com.vforce360.mar.ports.MarRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Mock Adapter for MarRepositoryPort.
 * Uses in-memory storage to simulate database behavior without real I/O.
 */
public class MockMarRepository implements MarRepositoryPort {

    private final Map<UUID, ModernizationAssessmentReport> database = new HashMap<>();

    public void addProjectReport(UUID projectId, ModernizationAssessmentReport report) {
        database.put(projectId, report);
    }

    @Override
    public Optional<ModernizationAssessmentReport> findByProjectId(UUID projectId) {
        return Optional.ofNullable(database.get(projectId));
    }
}
