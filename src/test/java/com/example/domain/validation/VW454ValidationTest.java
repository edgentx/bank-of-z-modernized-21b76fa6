package com.example.domain.validation;

import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackNotificationClient;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TDD Red Phase Test for Story ID: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * These tests verify that when a defect report is triggered,
 * the resulting Slack notification contains the correct GitHub URL.
 */
@DisplayName("VW-454: Slack Notification URL Validation")
class VW454ValidationTest {

    private GitHubIssuePort gitHubClient;
    private SlackNotificationPort slackClient;
    private DefectReportService defectReportService; // The class under test (to be implemented)

    private static final String ISSUE_ID = "VW-454";
    private static final String EXPECTED_URL = "https://github.com/bank-of-z/vforce360/issues/454";
    private static final String CHANNEL_NAME = "#vforce360-issues";

    @BeforeEach
    void setUp() {
        gitHubClient = new MockGitHubClient();
        slackClient = new MockSlackNotificationClient();
        
        // Inject mocks into the service (Simulating Spring Dependency Injection)
        defectReportService = new DefectReportService(gitHubClient, slackClient);
    }

    @Test
    @DisplayName("Should include GitHub issue URL in Slack body when reporting defect")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange: Configure the Mock GitHub client to return a valid URL
        ((MockGitHubClient) gitHubClient).setMockUrl(EXPECTED_URL);

        // Act: Trigger the defect report logic
        defectReportService.reportDefect(ISSUE_ID, CHANNEL_NAME);

        // Assert: Verify the Slack client received the message with the URL
        MockSlackNotificationClient mockSlack = (MockSlackNotificationClient) slackClient;
        assertThat(mockSlack.getSentMessages()).hasSize(1);

        MockSlackNotificationClient.Message sentMessage = mockSlack.getSentMessages().get(0);
        assertThat(sentMessage.channel()).isEqualTo(CHANNEL_NAME);
        
        // Core Validation: The body must contain the URL
        assertThat(sentMessage.body())
            .as("Slack body should contain the GitHub URL for the issue")
            .contains(EXPECTED_URL);
        
        assertThat(sentMessage.body())
            .as("Slack body should reference the Issue ID")
            .contains(ISSUE_ID);
    }

    @Test
    @DisplayName("Should handle missing GitHub URL gracefully")
    void testMissingGitHubUrl() {
        // Arrange: Configure Mock to return empty (simulating issue not found)
        ((MockGitHubClient) gitHubClient).setMockUrl(null);

        // Act & Assert: The service should handle this without crashing, 
        // potentially logging a warning or sending a message indicating the URL was not found.
        // For this red-phase test, we verify no exception is thrown and a message is sent.
        defectReportService.reportDefect(ISSUE_ID, CHANNEL_NAME);

        MockSlackNotificationClient mockSlack = (MockSlackNotificationClient) slackClient;
        assertThat(mockSlack.getSentMessages()).hasSize(1);
        
        // The body should explicitly mention URL lookup failure
        assertThat(mockSlack.getSentMessages().get(0).body())
            .contains("URL not found"); 
    }
}