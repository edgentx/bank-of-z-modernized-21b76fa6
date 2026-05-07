package com.example.e2e;

import com.example.domain.shared.DefectReportedEvent;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * 
 * Context: When a defect is reported via the Temporal worker,
 * the system should create a GitHub issue and notify Slack.
 * The Slack notification MUST contain the URL to the created GitHub issue.
 * 
 * Defect: The Slack body was missing the GitHub URL.
 */
public class VW454ValidationTest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubIssuePort githubPort;
    private ReportDefectWorkflow workflow; // The class under test (to be implemented)

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        // We mock a specific GitHub URL to verify it propagates correctly
        githubPort = new MockGitHubIssuePort("https://github.com/bank-of-z/issues/454");
        
        // Wire mocks to the workflow implementation (simulating Spring/Temporal injection)
        workflow = new ReportDefectWorkflowImpl(slackPort, githubPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl_RegressionVW454() {
        // Arrange
        String defectId = "VW-454";
        String title = "GitHub URL in Slack body";
        DefectReportedEvent event = new DefectReportedEvent(defectId, title, Instant.now());

        // Act
        // Trigger the workflow method corresponding to '_report_defect'
        workflow.reportDefect(event);

        // Assert
        // 1. Verify that a message was sent to Slack
        assertFalse(slackPort.sentMessages.isEmpty(), "Slack should have received a notification");

        // 2. Verify the message body contains the specific GitHub URL returned by the mock
        String actualSlackBody = slackPort.sentMessages.get(0);
        assertTrue(
            actualSlackBody.contains("https://github.com/bank-of-z/issues/454"),
            "Slack body should contain the GitHub URL: " + actualSlackBody
        );
    }

    @Test
    void testSlackBodyFormat_ShouldIncludeUrlTag() {
        // Arrange
        String defectId = "VW-454";
        DefectReportedEvent event = new DefectReportedEvent(defectId, "Validating URL format", Instant.now());

        // Act
        workflow.reportDefect(event);

        // Assert
        // Checking for the angle bracket format <url> often used in Slack to prevent unfurling
        String actualSlackBody = slackPort.sentMessages.get(0);
        assertTrue(
            actualSlackBody.contains("<https://github.com/bank-of-z/issues/454>"),
            "Slack body should contain the GitHub URL formatted as a link"
        );
    }

    // --- Stub / Placeholder Implementation for the Workflow to make code compile for Red Phase ---
    // In a real scenario, this class would be in src/main/java.
    // We include it here to ensure the tests have something to run against that initially fails.
    
    public static class ReportDefectWorkflowImpl {
        private final SlackNotificationPort slackPort;
        private final GitHubIssuePort githubPort;

        public ReportDefectWorkflowImpl(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
            this.slackPort = slackPort;
            this.githubPort = githubPort;
        }

        public void reportDefect(DefectReportedEvent event) {
            // This implementation is intentionally INCORRECT or EMPTY to cause the test to fail (Red Phase).
            // Actual implementation needs to create issue and send correct body to Slack.
            
            // Attempting to simulate the "Defect" state where URL might be missing:
            String issueUrl = githubPort.createIssue(event.title(), "Defect: " + event.type());
            
            // FAILING STATE: Sending a message without the URL (simulating the bug)
            // OR simply sending empty string to fail assertions.
            slackPort.send("Defect reported. Issue created at [MISSING LINK]"); 
        }
    }
}
