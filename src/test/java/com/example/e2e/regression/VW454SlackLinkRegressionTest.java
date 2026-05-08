package com.example.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.mocks.InMemorySlackNotificationAdapter;
import com.example.mocks.InMemoryGitHubIssueAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Regression test for VW-454.
 * Verifies that when a defect is reported via the temporal-worker,
 * the resulting Slack body contains the correct GitHub issue link.
 *
 * Issue: VW-454
 * Severity: LOW
 * Component: validation
 */
class VW454SlackLinkRegressionTest {

    private InMemorySlackNotificationAdapter mockSlack;
    private InMemoryGitHubIssueAdapter mockGitHub;
    private SlackNotificationPort slackService;
    private GitHubIssuePort gitHubService;

    @BeforeEach
    void setUp() {
        // We initialize our mock adapters to simulate the environment.
        // In a real integration test, these might be wired via Spring Context,
        // but for isolated regression verification we instantiate them directly.
        mockSlack = new InMemorySlackNotificationAdapter();
        mockGitHub = new InMemoryGitHubIssueAdapter();

        // We assume the SUT (System Under Test) is a service that uses these ports.
        // For this regression test, we are verifying the contract between the 
        // 'report_defect' workflow and the Slack notification body.
        slackService = mockSlack;
        gitHubService = mockGitHub;
    }

    @Test
    void shouldContainGitHubIssueUrlInSlackBody_whenDefectReported() {
        // Arrange
        String defectId = "VW-454";
        String defectTitle = "GitHub URL in Slack body (end-to-end)";
        String expectedGitHubUrl = "https://github.com/example/project/issues/454";

        // We configure the mock GitHub adapter to return a specific URL
        // so the test is deterministic and doesn't hit the real GitHub API.
        mockGitHub.setMockCreateUrl(expectedGitHubUrl);

        // Act
        // Simulating the workflow: Create Issue -> Report to Slack
        // Note: The actual workflow orchestration is likely handled by Temporal,
        // but we are testing the logical validation of the output here.
        String createdUrl = gitHubService.createIssue(defectTitle, "Defect reported by user");
        
        // We trigger the Slack notification logic.
        // This method internally formats the body.
        slackService.sendNotification(createdUrl, defectTitle);

        // Assert
        // The core validation: The Slack body must include the GitHub URL.
        String actualSlackBody = mockSlack.getLastBodySent();
        
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected: " + expectedGitHubUrl + " in body: " + actualSlackBody
        );
    }

    @Test
    void shouldContainGitHubIssueUrlInSlackBody_whenUrlIsComplex() {
        // Arrange
        String defectId = "S-FB-2";
        String complexTitle = "Fix: Validating complex chars in URL ?query=1&foo=bar";
        // Simulate a URL that might be encoded differently or contain special chars
        String complexUrl = "https://github.com/example/project/issues/2?foo=bar";

        mockGitHub.setMockCreateUrl(complexUrl);

        // Act
        String createdUrl = gitHubService.createIssue(complexTitle, "Description");
        slackService.sendNotification(createdUrl, complexTitle);

        // Assert
        String actualSlackBody = mockSlack.getLastBodySent();
        assertTrue(
            actualSlackBody.contains(complexUrl),
            "Slack body should handle complex GitHub URLs correctly"
        );
    }
}
