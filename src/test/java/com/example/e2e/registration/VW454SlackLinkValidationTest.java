package com.example.e2e.registration;

import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * Verifies that the Slack notification body includes the GitHub issue URL.
 *
 * <p>Context: The temporal worker executes a defect report workflow.
 * The side effect of this workflow is a notification sent to Slack.
 * This test validates the content of that notification.</p>
 */
class VW454SlackLinkValidationTest {

    private static final String TEST_CHANNEL = "#vforce360-issues";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/vforce360/issues/454";

    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    void reportDefectWorkflow_shouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        // Simulating the inputs expected by the Temporal workflow/activity
        String defectTitle = "Validating VW-454 — GitHub URL in Slack body";
        String defectDescription = "Checking link line...";

        // Act
        // In a real integration test, we would trigger the Temporal workflow.
        // Here, we simulate the Activity that posts to Slack, as that is the unit of behavior
        // defined in the Defect Description "Verify Slack body contains GitHub issue link".
        // This logic represents the 'Temporal Worker Executor' invoking the port.
        
        // NOTE: This class represents the RED phase. The actual class 
        // 'DefectReportingService' (or similar) does not exist yet or is empty.
        // We verify the behavior against the mock.
        
        // Simulated Workflow Execution
        triggerReportDefectActivity(
            mockSlack,
            TEST_CHANNEL,
            defectTitle,
            EXPECTED_GITHUB_URL
        );

        // Assert
        assertEquals(1, mockSlack.getMessages().size(), "Slack should receive one notification");
        
        MockSlackNotificationPort.PostedMessage posted = mockSlack.getMessages().get(0);
        assertEquals(TEST_CHANNEL, posted.channel, "Should post to the correct channel");
        
        // Core Validation: The body must contain the GitHub URL
        // Expected Behavior: "Slack body includes GitHub issue: <url>"
        assertTrue(
            posted.body.contains(EXPECTED_GITHUB_URL),
            "Slack body must contain the GitHub issue URL. Received: " + posted.body
        );
    }

    @Test
    void reportDefectWorkflow_slackBodyShouldContainFormattedGitHubLink() {
        // Arrange
        String defectTitle = "VW-454 Regression";
        String url = "https://github.com/bank-of-z/vforce360/issues/454";

        // Act
        triggerReportDefectActivity(mockSlack, TEST_CHANNEL, defectTitle, url);

        // Assert
        String body = mockSlack.getMessages().get(0).body;
        
        // Verify it's not just the raw URL string, but likely a clickable format or clearly identifiable
        assertTrue(body.contains("GitHub"), "Body should reference GitHub");
        assertTrue(body.contains(url), "Body must contain the specific issue URL");
    }

    // --- Helper Methods to simulate the SUT (System Under Test) ---
    // In a real project, these would exist in src/main/java.
    // We put them here to allow the test to compile and run (and FAIL).

    private void triggerReportDefectActivity(MockSlackNotificationPort slackPort, String channel, String title, String url) {
        // This is a placeholder for the logic we are about to write.
        // Currently, it just posts the title to satisfy compilation, but FAILS the assertion regarding the URL.
        String body = "Defect Reported: " + title; 
        
        // The defect is that the URL is missing or not formatted correctly.
        // This Red phase test ensures we don't implement the handler without the URL.
        slackPort.postMessage(channel, body);
    }
}
