package com.example.e2e;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.SlackNotifierPort;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression test for Story S-FB-1.
 * Verifies that when a defect is reported, the resulting Slack notification
 * contains the GitHub issue URL.
 * 
 * Component: validation
 * Severity: LOW
 */
class S_FB_1_DefectValidationTest {

    // This is a placeholder for the actual Workflow/Service implementation.
    // Since we are in TDD Red phase, this class likely doesn't exist yet
    // or doesn't implement the logic. We will mock the dependencies.
    
    @Test
    void shouldContainGitHubIssueUrlInSlackBody_whenReportDefectIsTriggered() {
        // Given
        MockSlackNotifier mockSlack = new MockSlackNotifier();
        // In a real Spring Boot test, we would inject this mock into the context
        // or pass it to the Workflow class. For this TDD red phase, we simulate
        // the execution manually since the implementing class is missing.
        
        String defectId = "VW-454";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        String description = "End-to-end validation failed.";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            title,
            description,
            Map.of("source", "VForce360 PM diagnostic")
        );

        // When
        // Simulating the temporal-worker exec trigger
        // This would normally be: defectWorkflow.report(cmd);
        // Since the implementation is missing, we manually invoke the behavior
        // we expect, OR we leave this to fail if we try to instantiate a class that doesn't exist.
        
        // For TDD Red, we assert against the Mock's state. 
        // If the logic doesn't exist, this test will fail because the URL won't be there.
        
        // STUB IMPLEMENTATION FOR RED PHASE:
        // We mimic what the 'real' code SHOULD do but incorrectly or not at all,
        // or we simply instantiate the mock and verify it's empty (Red).
        
        // Step 1: Trigger the logic (which currently doesn't exist or is stubbed)
        triggerReportDefect(cmd, mockSlack);

        // Then
        // Verification
        assertThat(mockSlack.getMessages())
            .as("Slack should have received a message")
            .isNotEmpty();

        MockSlackNotifier.SentMessage msg = mockSlack.getMessages().get(0);
        assertThat(msg.channel).isEqualTo("#vforce360-issues");
        assertThat(msg.body).contains("GitHub issue"); // Expected key text
        
        // The specific URL format check
        boolean hasUrl = msg.body.contains("http://github.com") || 
                         msg.body.contains("https://github.com");
        
        assertThat(hasUrl)
            .as("Slack body must contain a GitHub URL (<url>)")
            .isTrue();
    }

    /**
     * Temporary method to simulate the workflow execution.
     * In the Red phase, this method is empty or calls non-existent code,
     * causing the assertion below to fail.
     */
    private void triggerReportDefect(ReportDefectCmd cmd, SlackNotifierPort slack) {
        // RED PHASE: Implementation is missing.
        // If we did nothing, the mock list is empty, and the first assertion fails.
        // To simulate a specific bug report (Actual Behavior), we might send a message WITHOUT the URL.
        
        slack.sendMessage("#vforce360-issues", "Defect reported: " + cmd.title());
        // Note: The body above deliberately lacks the URL to simulate the 'Actual Behavior' 
        // or simply because the feature isn't built yet.
    }
}
