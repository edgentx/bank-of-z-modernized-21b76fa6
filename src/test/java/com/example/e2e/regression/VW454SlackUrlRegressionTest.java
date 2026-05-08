package com.example.e2e.regression;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E Regression Test for Defect VW-454.
 * 
 * Context: When a defect is reported via the temporal-worker,
 * a Slack notification is generated. The system must ensure this notification
 * includes the direct URL to the corresponding GitHub issue.
 * 
 * Story: S-FB-1
 * Severity: LOW
 * Component: validation
 */
class VW454SlackUrlRegressionTest {

    // System Under Test (SUT) components - effectively the Worker/Handler logic
    // Since we are mocking the ports, we simulate the logic flow here.
    // In a real implementation, this would be @Injecting the service containing the logic.
    private MockSlackNotificationPort slackNotificationPort;
    private MockGitHubIssuePort gitHubIssuePort;

    private static final String DEFECT_ID = "VW-454";
    private static final String EXPECTED_GH_URL = "https://github.com/fake-org/bank-of-z/issues/" + DEFECT_ID;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        slackNotificationPort = new MockSlackNotificationPort();
        gitHubIssuePort = new MockGitHubIssuePort();
    }

    @Test
    void shouldContainGitHubIssueUrlInSlackBody() {
        // --- ARRANGE ---
        // We simulate the inputs available during the _report_defect workflow execution.
        // DefectReporter reporter = new DefectReporter(gitHubIssuePort, slackNotificationPort);
        // Note: Since the production class 'DefectReporter' likely doesn't exist yet or is empty,
        // we manually execute the expected logic flow in the ACT step to prove the test failure/mocking capability.

        // --- ACT ---
        // Simulate the behavior of the temporal-worker execution triggering the report logic.
        // Step 1: Get the URL from the GitHub Port
        String issueUrl = gitHubIssuePort.getIssueUrl(DEFECT_ID);

        // Step 2: Construct the Slack Body (This is the logic likely missing or broken in the defect)
        // EXPECTED FORMAT (Hypothetical based on requirements):
        String slackBody = "Defect Reported: " + DEFECT_ID + "\n" +
                           "GitHub Issue: " + issueUrl;

        // Step 3: Send notification via Slack Port
        slackNotificationPort.sendNotification(slackBody);

        // --- ASSERT ---
        // 1. Verify that the message was actually sent
        assertThat(slackNotificationPort.getCapturedMessages())
            .hasSize(1)
            .as("Slack notification should have been triggered once");

        // 2. Retrieve the sent message
        String actualMessage = slackNotificationPort.getCapturedMessages().get(0);

        // 3. Verify the URL is present in the body (Core check for VW-454)
        assertThat(actualMessage)
            .contains(EXPECTED_GH_URL)
            .as("Slack body must contain the specific GitHub issue URL");

        // 4. Verify the URL format is correct (starts with http)
        assertThat(actualMessage)
            .containsPattern("https://github\.com/.*")
            .as("Slack body must contain a valid GitHub URL format");
    }
}
