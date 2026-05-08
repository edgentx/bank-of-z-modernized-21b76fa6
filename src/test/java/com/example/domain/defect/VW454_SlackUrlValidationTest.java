package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for VW-454.
 * 
 * Context: This test verifies that when a defect is reported, the resulting notification
 * sent to Slack contains the URL of the GitHub issue created for that defect.
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (missing URL).
 * 2. Regression test added to e2e/regression/ covering this scenario.
 * 
 * Note: This is a unit test serving as a regression test. In a full E2E scenario,
 * this would be wrapped in a Cucumber test (Gherkin), but the logic validation 
 * occurs here against the Domain Aggregate using Mock Adapters.
 */
class VW454_SlackUrlValidationTest {

    private MockSlackNotificationPort mockSlack;
    private MockGitHubIssuePort mockGitHub;
    private DefectAggregate defectAggregate; // Class under test (to be implemented)

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        mockGitHub = new MockGitHubIssuePort();
        // Assuming aggregate needs ports or a factory. For TDD, we might inject them or rely on a static accessor.
        // Given the existing patterns (CustomerAggregate), aggregates take an ID.
        defectAggregate = new DefectAggregate("test-defect-id", mockGitHub, mockSlack);
    }

    @Test
    void should_Include_GitHub_Url_In_Slack_Body_When_Defect_Reported() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";
        mockGitHub.setMockUrl(expectedGitHubUrl);
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-454",
            "VW-454: GitHub URL missing in Slack",
            "Validation error detected in VForce360 workflow",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // Act
        // The aggregate should execute the command, call GitHub, then call Slack with the URL.
        defectAggregate.execute(cmd);

        // Assert
        // 1. Verify GitHub was called (Implicitly by Slack receiving the URL)
        // 2. Verify Slack was called
        assertFalse(mockSlack.getSentMessages().isEmpty(), "Slack should have received a notification");
        
        MockSlackNotificationPort.SentMessage slackMsg = mockSlack.getSentMessages().get(0);
        assertEquals("#vforce360-issues", slackMsg.channel(), "Should post to the correct channel");
        
        // CORE ASSERTION FOR VW-454
        assertTrue(
            slackMsg.body().contains(expectedGitHubUrl), 
            "Slack body must contain the GitHub issue URL. Actual body: " + slackMsg.body()
        );

        // Check for the prefix mentioned in the Expected Behavior
        assertTrue(
            slackMsg.body().contains("GitHub issue:"),
            "Slack body should indicate the GitHub issue context"
        );
    }

    @Test
    void should_Handle_GitHub_Failure_Gracefully_And_Log_Slack_Error() {
        // Arrange
        mockGitHub.setShouldSucceed(false);
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-error", "Fail Test", "Desc", "HIGH", "proj-1"
        );

        // Act
        defectAggregate.execute(cmd);

        // Assert
        assertFalse(mockSlack.getSentMessages().isEmpty(), "Even if GitHub fails, internal logging occurs via Slack");
        String body = mockSlack.getSentMessages().get(0).body();
        assertTrue(body.contains("Failed to create GitHub issue"));
    }
}
