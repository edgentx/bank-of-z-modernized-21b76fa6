package com.example.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360DiagnosticsPort;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockVForce360DiagnosticsAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for S-FB-1.
 * Verifies that the Slack notification body generated via the Temporal defect report workflow
 * contains the correct GitHub issue URL.
 *
 * Context: Defect VW-454 reported missing URL in Slack body.
 */
class SlackValidationE2ETest {

    private MockVForce360DiagnosticsAdapter diagnosticsAdapter;
    private MockSlackNotificationAdapter slackAdapter;

    @BeforeEach
    void setUp() {
        diagnosticsAdapter = new MockVForce360DiagnosticsAdapter();
        slackAdapter = new MockSlackNotificationAdapter();
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_whenTriggeringReportDefectWorkflow() {
        // Arrange
        String defectTitle = "VW-454";
        String expectedGitHubUrl = "https://github.com/example-org/bank-of-z/issues/454";

        // Configure the mock diagnostic system to return a specific ID that maps to this URL
        diagnosticsAdapter.setResolvedGitHubUrl(expectedGitHubUrl);

        // Initialize the production service (simulated) with our mocks
        // In a real Spring Boot test, this would be @Autowired beans replaced by @MockBean
        // Here we follow the TDD Red Phase by invoking the flow logic directly or via a test wrapper.
        var workflow = new ReportDefectWorkflow(s diagnosticsAdapter, slackAdapter);

        // Act
        // Triggering the _report_defect via temporal-worker exec (simulation)
        workflow.execute(defectTitle);

        // Assert
        // Verify Slack body contains GitHub issue link
        // If the adapter wasn't called, getLatestPostedBody() returns null, failing the assertion
        String actualSlackBody = slackAdapter.getLatestPostedBody();

        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL: " + expectedGitHubUrl + "\nActual Body: " + actualSlackBody
        );
    }

    /**
     * Inner class representing the Workflow/Service under test.
     * This class acts as the bridge between the Diagnostics system and the Slack notification.
     * It simulates the logic that will be implemented in the Temporal Worker.
     */
    private static class ReportDefectWorkflow {
        private final VForce360DiagnosticsPort diagnostics;
        private final SlackNotificationPort slack;

        public ReportDefectWorkflow(VForce360DiagnosticsPort diagnostics, SlackNotificationPort slack) {
            this.diagnostics = diagnostics;
            this.slack = slack;
        }

        public void execute(String defectId) {
            // This is the logic that likely exists or needs to be fixed.
            // 1. Get details from VForce360
            String details = diagnostics.getDiagnosticContext(defectId);
            // 2. Generate the GitHub URL (simulated)
            String url = diagnostics.resolveGitHubUrl(defectId);
            // 3. Send to Slack
            String slackBody = "Defect Reported: " + details;
            
            // THE BUG IS LIKELY HERE: The URL isn't appended to the message.
            // This test ensures that `slackBody` *includes* `url`.
            // Since this is the RED phase, we expect the implementation to fail until fixed.
            slack.sendNotification(slackBody); 
        }
    }
}
