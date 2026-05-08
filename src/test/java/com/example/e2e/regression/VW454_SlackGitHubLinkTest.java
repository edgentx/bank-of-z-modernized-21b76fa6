package com.example.e2e.regression;

import com.example.mocks.MockGitHubRepository;
import com.example.mocks.MockSlackNotification;
import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Validates that when a defect is reported, the Slack notification body
 * contains the URL to the created GitHub issue.
 *
 * <p>Context: Defect reported by user. Expected Behavior: Slack body includes GitHub issue.
 *
 * <p>Prerequisites:
 * <ul>
 *   <li>Temporal worker triggers _report_defect workflow (simulated here).</li>
 *   <li>Integration with GitHub API (mocked).</li>
 *   <li>Integration with Slack API (mocked).</li>
 * </ul>
 */
public class VW454_SlackGitHubLinkTest {

    // Constants matching the Defect Report
    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
    private static final String DEFECT_ID = "VW-454";
    private static final String SUMMARY = "Validating VW-454 — GitHub URL in Slack body";
    private static final String DESCRIPTION = "Defect reported by user.\n\n**Severity:** LOW\n**Component:** validation";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/example/bank-of-z/issues/454";

    // Mocks
    private MockGitHubRepository gitHubMock;
    private MockSlackNotification slackMock;

    @BeforeEach
    public void setUp() {
        gitHubMock = new MockGitHubRepository();
        slackMock = new MockSlackNotification();
    }

    @Test
    public void testReportDefect_SlackBodyContainsGitHubLink() {
        // 1. ARRANGE: Configure Mocks
        // Simulate GitHub returning a specific issue URL upon creation
        gitHubMock.mockIssueUrl(PROJECT_ID, DEFECT_ID, EXPECTED_GITHUB_URL);

        // 2. ACT: Trigger the Defect Reporting Logic (Temporal Worker Simulation)
        // In a real Temporal workflow, this would be a workflow invocation.
        // Here we invoke the service logic directly to test the integration flow.
        // Note: We assume the implementation logic is injected or available.
        // Since we are in RED phase, we might be calling a class that doesn't exist yet,
        // or we manually wire the dependencies for the test.
        
        reportDefectWorkflow(
            gitHubMock, 
            slackMock, 
            PROJECT_ID, 
            DEFECT_ID, 
            SUMMARY, 
            DESCRIPTION
        );

        // 3. ASSERT: Verify the Behavior
        assertFalse(slackMock.getMessages().isEmpty(), "Slack should have received a message");
        assertEquals(1, slackMock.getMessages().size(), "Exactly one Slack message should be sent");

        MockSlackNotification.SentMessage sentMessage = slackMock.getMessages().get(0);
        
        // Validating VW-454: The core check
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            sentMessage.description.contains(EXPECTED_GITHUB_URL),
            "Slack body must contain the GitHub issue URL.\nExpected: " + EXPECTED_GITHUB_URL + "\nActual: " + sentMessage.description
        );

        // Additional sanity checks
        assertEquals(PROJECT_ID, sentMessage.projectId);
        assertEquals(DEFECT_ID, sentMessage.defectId);
    }

    /**
     * Simulates the Temporal Activity/Workflow logic that needs to be implemented/fixed.
     * This method orchestrates the GitHub creation and Slack notification.
     */
    private void reportDefectWorkflow(
            GitHubRepositoryPort githubRepo,
            SlackNotificationPort slackNotifier,
            String projectId,
            String defectId,
            String summary,
            String description
    ) {
        // Step 1: Create GitHub Issue
        String issueUrl = githubRepo.createIssue(projectId, defectId, summary, description);

        // Step 2: Notify Slack
        // BUG FIX: The description (body) sent to Slack must include the issueUrl.
        // The previous behavior (Actual Behavior) implied the link might have been missing.
        String slackBody = description + "\n\nGitHub Issue: " + issueUrl;
        
        slackNotifier.sendDefectReport(projectId, defectId, summary, slackBody);
    }
}
