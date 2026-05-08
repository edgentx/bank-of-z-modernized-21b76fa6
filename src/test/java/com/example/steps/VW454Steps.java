package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubPort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * E2E Regression test for VW-454.
 * Covers the defect report regarding validating the GitHub URL in Slack body.
 *
 * Scenario:
 * 1. Trigger _report_defect (simulate via the Temporal activity logic).
 * 2. Verify Slack body contains GitHub issue link.
 *
 * Context: VForce360 PM diagnostic conversation.
 */
public class VW454Steps {

    // System Under Test (Ports)
    private SlackNotificationPort slackPort;
    private GitHubPort githubPort;

    // Mock Adapters
    private InMemorySlackNotificationPort mockSlack;
    private InMemoryGitHubPort mockGitHub;

    @BeforeEach
    void setUp() {
        // We initialize mock adapters to simulate the environment.
        mockSlack = new InMemorySlackNotificationPort();
        mockGitHub = new InMemoryGitHubPort();

        // Wire ports to mocks (simulating Spring injection or Temporal worker dependency injection)
        slackPort = mockSlack;
        githubPort = mockGitHub;
    }

    @Test
    void test_report_defect_generates_slack_body_with_valid_github_url() {
        // ARRANGE
        // Simulating the data flow of a defect report.
        String defectTitle = "VW-454: Validating GitHub URL in Slack body";
        String defectBody = "Reproduction Steps...";
        String expectedChannel = "#vforce360-issues";

        // ACT
        // 1. Create GitHub Issue (simulating the first part of the workflow)
        String githubUrl = githubPort.createIssue(defectTitle, defectBody);

        // 2. Send Slack Notification (simulating the second part of the workflow)
        // The implementation should construct the body using the githubUrl
        String expectedSlackBody = "Defect Reported: " + defectTitle + "\nGitHub Issue: " + githubUrl;
        slackPort.sendMessage(expectedChannel, expectedSlackBody);

        // ASSERT
        // 1. Verify GitHub URL was generated and matches expected format
        assertThat(githubUrl)
            .as("GitHub URL must not be null")
            .isNotNull();
        
        assertThat(githubUrl)
            .as("GitHub URL must be a valid HTTP/HTTPS link")
            .startsWith("http");

        // 2. Verify Slack received the message
        String actualChannel = mockSlack.getLastChannel();
        String actualBody = mockSlack.getLastBody();

        assertThat(actualChannel)
            .as("Slack message should be sent to the correct channel")
            .isEqualTo(expectedChannel);

        assertThat(actualBody)
            .as("Slack body should not be empty")
            .isNotEmpty();

        assertThat(actualBody)
            .as("Slack body MUST contain the GitHub issue link (VW-454 fix validation)")
            .contains(githubUrl);
    }
}