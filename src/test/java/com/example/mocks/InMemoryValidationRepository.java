package com.example.mocks;

import com.example.domain.validation.model.DefectReportAggregate;
import com.example.domain.validation.port.GitHubIssuePort;
import com.example.domain.validation.port.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory repository for Validation aggregates.
 * This is a mock adapter as per the rules.
 */
public class InMemoryValidationRepository {
    private final Map<String, DefectReportAggregate> store = new HashMap<>();

    public void save(DefectReportAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
    }

    public DefectReportAggregate load(String defectId, GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        // For test simplicity, we might return a new instance or a stored one.
        // This allows us to test command execution on a fresh aggregate.
        return new DefectReportAggregate(defectId, gitHubPort, slackPort);
    }
}
