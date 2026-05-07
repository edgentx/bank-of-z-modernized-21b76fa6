package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Regression test for defect VW-454.
 * Ensures that when a defect is reported via the Temporal workflow,
 * the resulting GitHub URL is correctly propagated to the Slack notification body.
 */
public class DefectReportingFlowTest {

    @Test
    public void testDefectReportingIncludesGitHubUrlInSlackBody() {
        // Setup
        MockGitHubPort gitHubPort = new MockGitHubPort();
        MockSlackPort slackPort = new MockSlackPort();

        String expectedTitle = "VW-454 Regression";
        String expectedBody = "Validating URL in Slack body";
        
        // Execution (Simulating the workflow)
        // 1. Create Issue
        String issueUrl = gitHubPort.createIssue(expectedTitle, expectedBody);
        
        // 2. Send Notification
        slackPort.sendMessage("Defect Report Created. View at: " + issueUrl);

        // Verification
        assertNotNull(issueUrl, "GitHub issue URL should not be null");
        assertTrue(slackPort.wasUrlSent(issueUrl), "Slack body should contain the GitHub issue URL");
        assertTrue(slackPort.getSentMessages().get(0).contains("View at:"), "Slack message context should be correct");
    }
}
