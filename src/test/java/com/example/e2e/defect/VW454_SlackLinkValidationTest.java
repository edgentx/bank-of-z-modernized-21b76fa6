package com.example.e2e.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Regression test for Story VW-454.
 * <p>
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * <p>
 * Severity: LOW
 * Component: validation
 * <p>
 * This test validates the happy path: when a defect is reported via the temporal worker,
 * the resulting Slack notification body contains the link to the created GitHub issue.
 */
public class VW454_SlackLinkValidationTest {

    private MockGitHubIssuePort mockGitHubPort;
    private MockSlackNotificationPort mockSlackPort;
    private DefectAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockGitHubPort = new MockGitHubIssuePort();
        mockSlackPort = new MockSlackNotificationPort();
        // Initialize the aggregate with injected mocks
        aggregate = new DefectAggregate("defect-123", mockGitHubPort, mockSlackPort);
    }

    @Test
    @DisplayName("Given a valid ReportDefectCmd, when executed, then Slack body contains GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/test/repo/issues/1";
        String expectedChannel = "#vforce360-issues";

        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1",
            "Fix: Validating VW-454",
            "Defect reported by user.",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. Verify Aggregate Events
        assertThat(events)
            .hasSize(1)
            .allMatch(e -> e instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertThat(event.githubUrl()).isEqualTo(expectedGitHubUrl);

        // 2. Verify Slack Interaction (The Core VW-454 Requirement)
        String actualSlackBody = mockSlackPort.getLastMessageBody(expectedChannel);
        assertThat(actualSlackBody).isNotNull();
        assertThat(actualSlackBody).contains(expectedGitHubUrl);
        
        // Ensure the format is readable, not just technically containing the string
        assertThat(actualSlackBody).contains("GitHub Issue:");
    }

    @Test
    @DisplayName("Given Slack send fails, when executing ReportDefectCmd, then exception is thrown")
    public void testSlackFailurePropagation() {
        // Arrange
        mockSlackPort.setShouldFail(true);
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1", "Fail Test", "Desc", "HIGH", "proj-id"
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aggregate.execute(cmd));
    }

    @Test
    @DisplayName("Given successful report, the event state matches the Slack payload")
    public void testEventStateMatchesPayload() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "S-FB-1", "State Check", "Desc", "MEDIUM", "proj-id"
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        String slackBody = mockSlackPort.getLastMessageBody("#vforce360-issues");

        // Assert
        // The URL recorded in the domain event must match the one physically sent to Slack
        assertThat(slackBody).contains(event.githubUrl());
        assertThat(event.channel()).isEqualTo("#vforce360-issues");
    }
}
