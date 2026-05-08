package com.example.mocks;

import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce360.model.SlackNotification;
import com.example.domain.vforce360.ports.ReportDefectPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock adapter for ReportDefectPort.
 * Simulates the creation of a GitHub issue and returns a Slack notification string.
 * Used strictly for testing validation logic (VW-454) without real external I/O.
 */
@Component
public class MockReportDefectAdapter implements ReportDefectPort {

    private final String mockGitHubBaseUrl;

    public MockReportDefectAdapter() {
        // Default mock URL, configurable if needed
        this.mockGitHubBaseUrl = "https://github.com/mock-bank/issues";
    }

    @Override
    public SlackNotification reportDefect(ReportDefectCommand command) {
        // Simulate GitHub Issue Creation
        String mockIssueId = UUID.randomUUID().toString();
        String issueUrl = mockGitHubBaseUrl + "/" + mockIssueId;

        // Simulate Slack Body Construction
        // THIS IS THE SYSTEM UNDER TEST LOGIC FOR VW-454
        String body = String.format(
                "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                command.title(),
                command.severity(),
                issueUrl
        );

        return new SlackNotification("#vforce360-issues", body);
    }
}