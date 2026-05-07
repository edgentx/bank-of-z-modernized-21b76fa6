package com.e2e.regression;

import com.example.application.validation.ValidationService;
import com.example.ports.SlackNotifier;
import com.example.ports.GitHubIssueTracker;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotifier;
import com.example.mocks.MockGitHubIssueTracker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * Verifies that when a defect is reported, the Slack notification body
 * contains the GitHub issue URL.
 * 
 * Context: S-FB-1
 */
class Vw454ValidationSlackLinkE2ETest {

    /**
     * TC-01: Validating VW-454 — GitHub URL in Slack body.
     * 
     * Given a defect command for VW-454
     * When the validation service processes the report
     * Then the Slack message payload should include the GitHub Issue URL.
     */
    @Test
    void verifySlackBodyContainsGitHubUrl() {
        // 1. Setup Mocks (Adapters)
        MockGitHubIssueTracker gitHubMock = new MockGitHubIssueTracker();
        MockSlackNotifier slackMock = new MockSlackNotifier();
        
        // Configure GitHub Mock to return a specific URL (Simulating external API call)
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        gitHubMock.setMockIssueUrl(expectedUrl);

        // 2. Create System Under Test (Service wired with mock ports)
        ValidationService service = new ValidationService(gitHubMock, slackMock);

        // 3. Trigger the workflow (Temporal worker simulation)
        ReportDefectCmd cmd = new ReportDefectCmd(
            "agg-123",
            "VW-454",
            "GitHub URL missing in Slack body",
            "LOW"
        );

        // 4. Execute
        service.reportDefect(cmd);

        // 5. Verify the Contract (The "Red Phase" failure if implementation is missing)
        String actualSlackMessage = slackMock.getLastPostedMessage();
        
        assertNotNull(actualSlackMessage, "Slack message should not be null");
        assertTrue(
            actualSlackMessage.contains(expectedUrl),
            "Slack body must include the GitHub issue URL: " + expectedUrl + "\nActual Body: " + actualSlackMessage
        );
        
        // Verify the specific format expected by the story
        assertTrue(
            actualSlackMessage.contains("GitHub issue:"),
            "Slack body should explicitly mention 'GitHub issue:'"
        );
    }

    @Test
    void verifySlackBodyContainsGitHubLinkPattern() {
        // Additional check to ensure the link structure is correct
        MockGitHubIssueTracker gitHubMock = new MockGitHubIssueTracker();
        MockSlackNotifier slackMock = new MockSlackNotifier();
        
        String mockUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        gitHubMock.setMockIssueUrl(mockUrl);

        ValidationService service = new ValidationService(gitHubMock, slackMock);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "agg-456",
            "VW-454",
            "Test",
            "LOW"
        );

        service.reportDefect(cmd);

        String body = slackMock.getLastPostedMessage();
        
        // Assert that the URL is present
        assertTrue(body.contains(mockUrl));
    }
}
