package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * S-FB-1: Regression Test for GitHub URL validation in Slack body.
 *
 * Context: Defect VW-454 reported that the Slack body for a defect report
 * did not contain the GitHub issue link.
 *
 * Expected Behavior: Slack body includes GitHub issue: <url>.
 */
class SFB1_DefectValidationRegressionTest {

    private MockGitHubPort gitHubPort;
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    @DisplayName("S-FB-1: Verify defect reporting workflow includes GitHub URL in Slack body")
    void verifyGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String channel = "#vforce360-issues";
        String expectedUrl = "https://github.com/vforce360/issues/454";

        // Command representing the temporal trigger
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, channel);

        // Act
        // Create the aggregate with mock adapters
        DefectAggregate aggregate = new DefectAggregate(defectId, gitHubPort, slackPort);
        aggregate.execute(cmd);

        // Assert
        // 1. Verify GitHub port was queried correctly
        assertThat(gitHubPort.getLastQueriedId()).isEqualTo(defectId);

        // 2. Verify Slack received exactly one message
        assertThat(slackPort.getPostedMessages()).hasSize(1);

        // 3. CRITICAL ASSERTION: Verify the message body contains the GitHub URL
        MockSlackNotificationPort.SlackMessage postedMsg = slackPort.getPostedMessages().get(0);
        assertThat(postedMsg.channel()).isEqualTo(channel);
        assertThat(postedMsg.body())
                .as("Slack body must contain the GitHub URL for the defect")
                .contains(expectedUrl);

        // 4. Verify domain event was emitted
        assertThat(aggregate.uncommittedEvents()).hasSize(1);
        DefectReportedEvent event = (DefectReportedEvent) aggregate.uncommittedEvents().get(0);
        assertThat(event.messageBody()).contains(expectedUrl);
    }

    @Test
    @DisplayName("S-FB-1: Ensure Slack body structure is correct (Regression check)")
    void verifySlackBodyStructure() {
        // Arrange
        String defectId = "VW-999"; // Different ID to ensure dynamic handling
        String channel = "#vforce360-issues";
        
        // Configure Mock
        gitHubPort = new MockGitHubPort(); // Fresh mock without default VW-454 override if needed
        // Note: In a real scenario, the MockGitHubPort might need setup for VW-999.
        // For this test, we rely on the generic fallback or specific behavior.
        
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, channel);

        // Act
        DefectAggregate aggregate = new DefectAggregate(defectId, gitHubPort, slackPort);
        aggregate.execute(cmd);

        // Assert
        String body = slackPort.getPostedMessages().get(0).body();
        
        // Validate that "GitHub Issue:" label exists in the body
        assertThat(body).contains("GitHub Issue:");
        assertThat(body).contains("https://github.com/");
    }
}