package com.vforce360.tests.mocks;

import com.vforce360.model.ReportIdentifier;
import com.vforce360.ports.IModernizationReportRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock adapter for the Report Repository.
 * Allows deterministic testing of the service layer without external databases.
 */
public class MockModernizationReportRepository implements IModernizationReportRepository {

    private final Map<String, ReportData> database = new HashMap<>();
    private String forcedRawContent = null;

    public void clear() {
        database.clear();
        forcedRawContent = null;
    }

    public void givenRawContentIs(String content) {
        this.forcedRawContent = content;
    }

    public void seedReport(String projectId, String rawContent) {
        String id = "report-" + projectId;
        database.put(id, new ReportData(id, projectId, rawContent));
    }

    @Override
    public Optional<String> findRawContentByProjectId(String projectId) {
        // Simulate direct DB query returning raw string
        if (forcedRawContent != null) {
            return Optional.of(forcedRawContent);
        }
        return database.values().stream()
                .filter(r -> r.projectId().equals(projectId))
                .map(ReportData::content)
                .findFirst();
    }

    @Override
    public Optional<ReportData> findById(ReportIdentifier reportId) {
        return Optional.ofNullable(database.get(reportId.value()));
    }
}
