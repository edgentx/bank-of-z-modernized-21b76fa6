package com.example.e2e.regression;

import com.example.mocks.MockIssueTrackingPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.IssueTrackingPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * 
 * <h2>Story Context</h2>
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * <p>
 * When a defect is reported via the temporal worker:
 * <ol>
 *   <li>An external issue (e.g., in GitHub) should be created.</li>
 *   <li>A notification should be sent to Slack.</li>
 *   <li>The Slack notification MUST contain the URL to the created GitHub issue.</li>
 * </ol>
 * 
 * <h2>Test Strategy (Red Phase)</h2>
 * This test mocks the external dependencies (GitHub/Slack) to verify the wiring logic.
 * It explicitly checks that the URL passed from the 'GitHub' mock appears in the 
 * inputs of the 'Slack' mock.
 */
public class VW454ValidationE2ETest {

    // SUT: The orchestrator that bridges Temporal -> Domain -> Adapters
    // In a real Spring Boot app, this would likely be an @Service bean.
    // For this red-phase test structure, we simulate the component under test.
    private DefectReportWorkflow defectWorkflow;

    // Mocks for external ports
    private MockIssueTrackingPort mockIssueTracking;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        // Initialize Mocks
        mockIssueTracking = new MockIssueTrackingPort();
        mockSlack = new MockSlackNotificationPort();

        // Inject mocks into the SUT.
        // Note: We are creating a lightweight instance of the class we intend to write.
        defectWorkflow = new DefectReportWorkflow(mockIssueTracking, mockSlack);
    }

    @Test
    @SuppressWarnings("null")
    void testSlackBodyContainsGitHubIssueUrl() {
        // Given: A valid defect report triggered by the user
        String defectTitle = "VW-454: GitHub URL missing from Slack";
        String defectDescription = "When reporting a defect, the link to the GitHub issue is not appearing in the Slack notification.";
        String targetChannel = "#vforce360-issues";

        // When: The workflow is executed
        defectWorkflow.reportDefect(targetChannel, defectTitle, defectDescription);

        // Then: We verify the contract between Issue Tracking and Slack
        List<MockSlackNotificationPort.PostedMessage> messages = mockSlack.getPostedMessages();
        
        // 1. Verify a message was actually sent to Slack
        assertFalse(messages.isEmpty(), "Slack should have received a notification");
        
        // 2. Verify it was sent to the correct channel
        MockSlackNotificationPort.PostedMessage posted = messages.get(0);
        assertEquals(targetChannel, posted.channel, "Notification should go to the correct channel");

        // 3. CRITICAL ASSERTION for VW-454:
        // The issue tracker mock generated a URL. The Slack body MUST contain this URL.
        // We simulate retrieving the URL that the system 'should' have generated.
        // Ideally, we capture it from the mock state.
        
        // To do this strictly in the red phase without existing implementation code:
        // We re-run the logic locally to determine what the URL *would* be, 
        // or (better) we inspect the mocks to ensure the data flowed.
        // Here, we will manually calculate the expected URL based on MockIssueTrackingPort's behavior
        // to verify the SUT passed it through.
        
        String expectedUrlSuffix = "1"; // Default mock behavior
        // Note: In a real scenario, we might expose the last created URL from the MockIssueTrackingPort
        // or simply verify that the Slack body contains a valid URL pattern.
        
        // For the purpose of VW-454 validation, checking the presence of the link is key.
        // Since we control the Mocks, we can check if the 'Link' appears in the 'Body'.
        
        // Let's assume the MockIssueTrackingPort returned a specific URL we can assert against.
        // We need to make the mock return a predictable value or retrieve what it returned.
        
        // Refined Assertion: Check that the body is not empty/null (Baseline)
        assertNotNull(posted.body, "Slack message body should not be null");
        
        // Refined Assertion: Check that the body contains a URL (The specific defect)
        // Since we can't access the 'return value' of the port from the previous step easily 
        // without stateful mocks, we will check for the generic structure expected 
        // or verify the 'MockIssueTrackingPort' was called and that data ended up in Slack.
        
        // However, to be precise for VW-454: "Slack body includes GitHub issue: <url>"
        // We will verify the URL exists in the body. We need the specific URL.
        // Let's refine the Mock to capture its own output for verification.
        
        String expectedUrl = "http://github.example.com/mock-repo/issues/1";
        
        // THIS IS THE FAILING TEST (Red Phase)
        // The body currently won't have this, or the method doesn't exist.
        assertTrue(
            posted.body.contains(expectedUrl), 
            "Slack body should contain the GitHub issue URL created by the system. " +
            "Expected to find: [" + expectedUrl + "] within body: [" + posted.body + "]"
        );
    }

    /**
     * Placeholder for the System Under Test (SUT).
     * This class represents the Temporal Workflow Activity or Service 
     * that handles the defect reporting logic.
     * 
     * This code is NOT part of the test, but part of the 'Implementation' side 
     * which is currently missing/stubbed.
     */
    public static class DefectReportWorkflow {
        private final IssueTrackingPort issueTracker;
        private final SlackNotificationPort slackNotifier;

        public DefectReportWorkflow(IssueTrackingPort issueTracker, SlackNotificationPort slackNotifier) {
            this.issueTracker = issueTracker;
            this.slackNotifier = slackNotifier;
        }

        public void reportDefect(String channel, String title, String description) {
            // Implementation Missing:
            // 1. Create issue in tracker
            // 2. Compose Slack message with URL
            // 3. Send Slack notification
            
            // Currently does nothing or throws error -> Test Fails (Red)
            throw new UnsupportedOperationException("DefectReportWorkflow.reportDefect not implemented yet");
        }
    }
}
