package com.example.domain.validation;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * 
 * Story ID: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 * 
 * This test acts as the "Red Phase" of TDD. It will fail because the
 * DefectReportingWorkflow implementation does not yet exist or does not
 * correctly append the GitHub URL to the Slack body.
 */
public class VW454SlackLinkRegressionTest {

    private MockSlackNotificationPort slackMock;
    private MockGitHubIssuePort githubMock;

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackNotificationPort();
        githubMock = new MockGitHubIssuePort();
    }

    /**
     * Simulates the _report_defect temporal worker execution.
     * Verifies that the generated Slack message body contains the expected GitHub URL.
     */
    @Test
    void testReportDefect_generatesSlackBodyContainingGitHubLink() {
        // Arrange
        String defectId = "VW-454";
        String channel = "#vforce360-issues";
        String description = "Validating GitHub URL in Slack body";
        
        // The expected URL based on our mock configuration
        String expectedUrl = githubMock.getIssueUrl(defectId);

        // System Under Test (SUT)
        // In a real scenario, this would be injected or instantiated via Spring Context.
        // For the unit test, we instantiate the workflow (or handler) directly.
        // Note: This class is assumed not to exist yet or lacks the feature.
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(slackMock, githubMock);

        // Act
        // Trigger the workflow execution as described in Reproduction Steps
        workflow.reportDefect(channel, defectId, description);

        // Assert
        // 1. Verify a message was actually sent
        assertEquals(1, slackMock.getMessages().size(), "Slack message should have been posted");

        // 2. Verify the target channel is correct
        MockSlackNotificationPort.Message posted = slackMock.getMessages().get(0);
        assertEquals(channel, posted.channel(), "Message should be sent to the correct channel");

        // 3. CRITICAL ASSERTION: Verify the body contains the GitHub link
        // This is the core acceptance criterion for the defect fix.
        assertTrue(
            posted.body().contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected to find: " + expectedUrl + " in body: " + posted.body()
        );
    }

    /**
     * Placeholder class for the System Under Test (SUT).
     * This file represents the "missing" implementation that the engineer must write
     * to make the test pass (Green phase).
     * 
     * DO NOT INCLUDE this class in the final production output if it already exists
     * in a different location, but for the purpose of this test file compilation,
     * it is defined here to simulate the dependency.
     */
    public static class DefectReportingWorkflow {
        private final SlackNotificationPort slackPort;
        private final GitHubIssuePort githubPort;

        public DefectReportingWorkflow(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
            this.slackPort = slackPort;
            this.githubPort = githubPort;
        }

        public void reportDefect(String channel, String issueId, String description) {
            // This implementation is intentionally INCORRECT or Stubbed
            // to simulate the "Red" phase of TDD prior to the fix.
            // Current behavior (Hypothetical defect):
            // It only sends the description, forgetting the link.
            
            String body = "Defect Reported: " + description; 
            // Missing: String url = githubPort.getIssueUrl(issueId);
            // Missing: body += "\n" + url;
            
            slackPort.postMessage(channel, body);
        }
    }
}
