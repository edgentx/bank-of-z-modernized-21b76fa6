package com.vforce360.adapters;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.MarReportPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of MarReportPort to replace database dependency.
 * This resolves the 'findByProjectId' missing implementation error.
 */
@Component
public class JpaMarReportAdapter implements MarReportPort {

    private final ConcurrentHashMap<String, ModernizationAssessmentReport> store = new ConcurrentHashMap<>();

    @Override
    public ModernizationAssessmentReport save(ModernizationAssessmentReport report) {
        store.put(report.getProjectId(), report);
        return report;
    }

    @Override
    public Optional<ModernizationAssessmentReport> findByProjectId(String projectId) {
        return Optional.ofNullable(store.get(projectId));
    }
}