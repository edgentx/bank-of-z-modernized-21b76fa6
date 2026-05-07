package com.example.domain.validation;

import com.example.domain.shared.ValidationReportedEvent;
import com.example.mocks.MockGitHubIntegrationPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIntegrationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TDD Red Phase Tests for Story S-FB-1.
 * 
 * These tests validate that the defect reporting workflow (triggered via Temporal/Temporal-worker exec)
 * correctly generates a Slack notification body that includes the GitHub issue URL.
 */
class DefectReportingWorkflowTest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubIntegrationPort githubPort;
    private DefectReportingWorkflow workflow;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        githubPort = new MockGitHubIntegrationPort();
        
        // We inject the mocks into the workflow class (or Activity implementation)
        workflow = new DefectReportingWorkflow(slackPort, githubPort);
    }

    @Test
    @DisplayName("S-FB-1: When defect is reported and GitHub URL exists, Slack body should contain the URL")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "S-FB-1";
        String expectedGithubUrl = "https://github.com/example/bank-of-z/issues/454";
        
        githubPort.setMockUrl(expectedGithubUrl);
        
        ValidationReportedEvent event = new ValidationReportedEvent(
            UUID.randomUUID().toString(),
            "Validating VW-454",
            "https://github.com/example/bank-of-z/issues/454", // This is the data we want to see in Slack
            Instant.now()
        );

        // Act
        workflow.handleDefectReport(event);

        // Assert
        List<String> messages = slackPort.getSentMessages();
        assertThat(messages).hasSize(1);
        
        String slackBody = messages.get(0);
        assertThat(slackBody)
            .as("Slack body should include the GitHub issue URL")
            .contains(expectedGithubUrl);
            
        assertThat(slackBody)
            .as("Slack body should mention 'GitHub issue' context")
            .contains("GitHub issue");
    }

    @Test
    @DisplayName("S-FB-1: When GitHub URL is missing, Slack body should indicate missing info")
    void testSlackBodyHandlesMissingUrl() {
        // Arrange
        githubPort.setEmptyResponse(true);
        
        ValidationReportedEvent event = new ValidationReportedEvent(
            UUID.randomUUID().toString(),
            "Validating VW-454",
            null, // Missing URL
            Instant.now()
        );

        // Act
        workflow.handleDefectReport(event);

        // Assert
        List<String> messages = slackPort.getSentMessages();
        assertThat(messages).hasSize(1);
        
        String slackBody = messages.get(0);
        // Verify that the system handles the missing URL gracefully (or fails explicitly depending on requirements)
        // Based on AC "Slack body includes GitHub issue: <url>", we expect the link.
        // If the link is missing from the event, we should probably not send a broken link.
        assertThat(slackBody).doesNotContain("https://github.com/");
    }

    @Test
    @DisplayName("S-FB-1: Workflow should fail if Slack notification fails")
    void testWorkflowFailureOnSlackError() {
        // Arrange
        SlackNotificationPort failingPort = () -> { throw new RuntimeException("Slack API Timeout"); };
        DefectReportingWorkflow failingWorkflow = new DefectReportingWorkflow(failingPort, githubPort);
        
        ValidationReportedEvent event = new ValidationReportedEvent(
            UUID.randomUUID().toString(),
            "Validating VW-454",
            "https://github.com/example/bank-of-z/issues/454",
            Instant.now()
        );

        // Act & Assert
        // In Temporal, this might result in a retry, but for unit testing we verify the exception propagation
        assertThrows(RuntimeException.class, () -> {
            failingWorkflow.handleDefectReport(event);
        });
    }
}
