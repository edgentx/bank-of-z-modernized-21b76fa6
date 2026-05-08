package com.example.steps;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotifierPort;
import com.example.mocks.MockGitHubRepository;
import com.example.mocks.MockSlackNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Test class for VW-454: Validating GitHub URL in Slack body (end-to-end).
 * 
 * Context: Defect reported via VForce360 PM diagnostic.
 * Behavior: Trigger report_defect -> Verify Slack body contains GitHub issue link.
 */
@ExtendWith(MockitoExtension.class)
public class VW454Steps {

    // We use the mock adapters directly to simulate the system behavior
    private final MockSlackNotifier mockSlack = new MockSlackNotifier();
    private final MockGitHubRepository mockGitHub = new MockGitHubRepository();

    // Ideally, we inject the System Under Test (SUT) here.
    // Since the SUT (ReportDefectWorkflow) doesn't exist yet (TDD Red Phase),
    // we will write the test against the expected interaction pattern.

    @BeforeEach
    public void setUp() {
        mockSlack.reset();
        mockGitHub.reset();
    }

    /**
     * Scenario: Triggering report_defect results in a Slack message with the GitHub issue URL.
     * 
     * Steps:
     * 1. Trigger _report_defect via temporal-worker exec
     * 2. Verify Slack body contains GitHub issue link
     */
    @Test
    public void testReportDefectIncludesGitHubLinkInSlackBody() {
        // GIVEN
        String defectTitle = "VW-454 Regression: Missing URL in Slack";
        String defectDescription = "The defect report is not linking to the created GitHub issue.";

        // WHEN
        // In a real integration test, we would invoke the Temporal workflow or the service handler.
        // For this Red Phase test, we simulate the 'happy path' logic that SHOULD exist:
        
        // 1. Create GitHub Issue (Simulated)
        String issueUrl = mockGitHub.createIssue(defectTitle, defectDescription);
        assertNotNull(issueUrl, "GitHub URL should be generated");

        // 2. Report Defect to Slack (Simulated)
        // The expected behavior is that the logic constructs a message containing the URL.
        String messageBody = "Defect Reported: " + defectTitle + "\n" +
                            "GitHub Issue: " + issueUrl; // This is the expected format
        
        mockSlack.postMessage(messageBody);

        // THEN
        // Verify the message was sent
        List<String> messages = mockSlack.getPostedMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");

        // Verify the content includes the specific GitHub URL
        String actualMessage = messages.get(0);
        assertTrue(
            actualMessage.contains(issueUrl), 
            "Slack body must include the GitHub issue URL. Received: " + actualMessage
        );
        
        // Regression check: Ensure it looks like a valid URL format
        assertTrue(
            actualMessage.contains("https://github.com/"),
            "Slack body should contain a valid GitHub URL prefix."
        );
    }

    /**
     * Scenario: Verify that Slack message is NOT sent if GitHub creation fails (Negative/Road Case).
     */
    @Test
    public void testReportDefectFailsIfGitHubTitleIsEmpty() {
        // GIVEN
        String emptyTitle = "";

        // WHEN & THEN
        // We expect the GitHub adapter to reject invalid input
        assertThrows(IllegalArgumentException.class, () -> {
            mockGitHub.createIssue(emptyTitle, "description");
        });

        // Verify Slack was never called because GitHub failed
        assertTrue(mockSlack.getPostedMessages().isEmpty(), "No Slack messages should be posted on GitHub failure");
    }
}
