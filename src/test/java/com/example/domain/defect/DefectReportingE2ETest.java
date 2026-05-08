package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubIssueClient;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotifierPort;
import com.example.services.DefectReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E / Regression Test for S-FB-1.
 * Validates that when a defect is reported, the Slack notification body
 * contains the GitHub issue URL.
 */
class DefectReportingE2ETest {

    private DefectRepository defectRepository;
    private MockGitHubIssueClient gitHubClient;
    private MockSlackNotifier slackNotifier;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        defectRepository = new InMemoryDefectRepository();
        gitHubClient = new MockGitHubIssueClient();
        slackNotifier = new MockSlackNotifier();
        
        service = new DefectReportingService(defectRepository, gitHubClient, slackNotifier);
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBodyWhenDefectReported() {
        // Arrange
        String defectId = "VW-454";
        String expectedTitle = "Validating VW-454";
        String expectedSeverity = "LOW";
        String fakeGitHubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        gitHubClient.setMockUrl(fakeGitHubUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            expectedTitle, 
            "Description of the defect", 
            DefectAggregate.Severity.LOW
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        assertThat(slackNotifier.notifications).hasSize(1);
        
        MockSlackNotifier.CapturedNotification notification = slackNotifier.notifications.get(0);
        String slackBody = notification.messageBody;
        
        // The acceptance criteria: "Slack body includes GitHub issue: <url>"
        assertThat(slackBody)
            .as("Slack body must contain the GitHub issue URL")
            .contains(fakeGitHubUrl);
    }

    @Test
    void shouldHandleGitHubFailureGracefully() {
        // Arrange
        String defectId = "VW-455";
        gitHubClient.setShouldReturnEmpty(true);

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "GitHub is down", 
            "Service unavailable", 
            DefectAggregate.Severity.MEDIUM
        );

        // Act
        // We expect the service to handle this, perhaps logging or falling back.
        // For the TDD red phase, we define the expected behavior: 
        // If GitHub fails, the Slack body should contain a fallback message.
        service.reportDefect(cmd);

        // Assert
        assertThat(slackNotifier.notifications).hasSize(1);
        String slackBody = slackNotifier.notifications.get(0).messageBody();
        assertThat(slackBody).contains("Failed to create GitHub issue");
    }
}
