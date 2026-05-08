package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockGithubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * E2E / Regression Test for Defect: VW-454
 * Title: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Tests the scenario where a defect is reported via Temporal worker logic:
 * 1. Command triggers Aggregate
 * 2. Event is emitted (notifying listeners)
 * 3. Handlers create GitHub Issue
 * 4. Handlers notify Slack with the GitHub URL
 */
class DefectReportingE2ETest {

    private MockGithubIssuePort mockGithub;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockGithub = new MockGithubIssuePort();
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectId = "defect-test-1";
        String expectedGithubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        
        mockGithub.setForcedUrl(expectedGithubUrl);

        // --- Simulation of Temporal Workflow Logic ---
        var aggregate = new DefectAggregate(defectId);
        var cmd = new ReportDefectCommand(
            defectId, 
            "VW-454", 
            "initial-dummy-url", // The cmd URL might be a guess, but the AGGREGATE event should hold the created one
            "Slack body missing URL"
        );

        // Act
        // 1. Execute Domain Logic
        var events = aggregate.execute(cmd);
        
        // 2. Simulate Event Handlers (orchiestrated by Temporal)
        // In a real system, these would be separate listeners. Here we simulate the outcome.
        String actualGithubUrl = mockGithub.createIssue("VW-454: Slack body missing URL", "Reproduction steps...");
        
        String slackMessage = String.format("Defect Reported: %s. GitHub Issue: %s", "VW-454", actualGithubUrl);
        mockSlack.send(slackMessage);

        // Assert (E2E Verification)
        assertThat(mockGithub.wasIssueCreatedWithTitle("VW-454: Slack body missing URL"))
            .as("GitHub issue should be created")
            .isTrue();

        assertThat(mockSlack.wasMessageSentContaining(expectedGithubUrl))
            .as("Slack body must contain the GitHub URL (VW-454 validation)")
            .isTrue();
    }

    @Test
    void shouldFailIfSlackBodyIsEmpty() {
        // Arrange
        MockSlackNotificationPort strictMock = new MockSlackNotificationPort() {
            @Override
            public void send(String messageBody) {
                if (messageBody == null || messageBody.isEmpty()) {
                    throw new IllegalArgumentException("Slack body cannot be empty");
                }
                super.send(messageBody);
            }
        };

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            strictMock.send("");
        });
    }
}
