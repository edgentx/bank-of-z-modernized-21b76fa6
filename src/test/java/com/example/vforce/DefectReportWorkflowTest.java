package com.example.vforce;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * This test ensures that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The resulting URL is included in the Slack notification body.
 */
class DefectReportWorkflowTest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubPort gitHubPort;
    private DefectReportWorkflow workflow;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubPort();
        workflow = new DefectReportWorkflow(slackPort, gitHubPort);
    }

    @Test
    void testReportDefect_SlackBodyContainsGitHubUrl() {
        // ARRANGE
        String defectTitle = "VW-454: Validation Failure";
        String defectDescription = "The validation step is failing.";
        String expectedChannel = "#vforce360-issues";
        String expectedGitHubUrl = "https://github.com/fake-org/bank-of-z/issues/454";

        // Configure the mock GitHub port to return a specific URL
        gitHubPort.setNextIssueUrl(expectedGitHubUrl);

        // ACT
        // This method is expected to:
        // 1. Call GitHubPort.createIssue
        // 2. Call SlackNotificationPort.sendMessage with the URL embedded in the body
        workflow.reportDefect(defectTitle, defectDescription);

        // ASSERT
        // 1. Verify Slack was called
        assertEquals(1, slackPort.getMessages().size(), "Slack should receive exactly one message");

        // 2. Verify the Channel
        var sentMessage = slackPort.getMessages().get(0);
        assertEquals(expectedChannel, sentMessage.channel(), "Message should be sent to #vforce360-issues");

        // 3. VERIFY THE FIX: Check that the body contains the GitHub Issue URL
        assertTrue(
            sentMessage.body().contains(expectedGitHubUrl),
            "Slack body must contain the URL: " + expectedGitHubUrl + "\nActual body: " + sentMessage.body()
        );
        
        // 4. Ensure the format is somewhat readable (contains a label)
        assertTrue(
            sentMessage.body().toLowerCase().contains("github issue") || sentMessage.body().toLowerCase().contains("link"),
            "Slack body should ideally indicate what the URL is for (accessibility/context)."
        );
    }

    @Test
    void testReportDefect_GitHubFailureDoesNotSendSlack() {
        // ARRANGE
        gitHubPort.setShouldFail(true);

        // ACT
        workflow.reportDefect("Error", "Details");

        // ASSERT
        assertEquals(0, slackPort.getMessages().size(), "No Slack message should be sent if GitHub issue creation fails");
    }
}