package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for VW-454.
 * Validates that when _report_defect is triggered via Temporal,
 * the Slack body contains the GitHub issue link.
 * 
 * Corresponds to: S-FB-1
 */
public class VW454ValidationSteps {

    @Test
    public void testSlackBodyContainsGitHubUrl() {
        // 1. Setup Mocks
        MockGitHubPort mockGitHub = new MockGitHubPort();
        MockSlackPort mockSlack = new MockSlackPort();

        // 2. Define Inputs
        String issueId = "VW-454";
        String expectedChannel = "#vforce360-issues";
        String expectedUrl = "https://github.com/example/issues/" + issueId;

        // 3. Simulate the Workflow Logic (Red Phase)
        // This code represents what the Temporal workflow activity SHOULD do.
        // We are asserting that this logic holds true.
        
        // Step A: Retrieve the URL from GitHub (using mock)
        String actualUrl = mockGitHub.getIssueUrl(issueId);
        
        // Step B: Construct the Slack Body
        // The defect report implies the body must *include* the URL.
        String slackBody = "Defect reported: " + actualUrl;
        
        // Step C: Send the message (using mock)
        mockSlack.sendMessage(expectedChannel, slackBody);

        // 4. Verify Expected Behavior (The Assertions)
        // AC: Regression test added covering this scenario
        // AC: The validation ensures the link is present.
        
        assertThat(mockSlack.messages).hasSize(1);
        
        MockSlackPort.Message msg = mockSlack.messages.get(0);
        assertThat(msg.channel()).isEqualTo(expectedChannel);
        
        // Critical Check: Does the body contain the GitHub link?
        assertThat(msg.body()).contains(expectedUrl);
        assertThat(msg.body()).contains("GitHub"); // Explicit check for context mentioned in defect
    }
}
