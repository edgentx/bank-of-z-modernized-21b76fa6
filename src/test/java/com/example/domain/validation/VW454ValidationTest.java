package com.example.domain.validation;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD Red Phase Tests for S-FB-1: Validating VW-454.
 * Scenario: Triggering report_defect ensures the Slack body contains the GitHub URL.
 * 
 * These tests cover the regression requirement that the GitHub URL is present
 * in the Slack notification body after a defect is reported.
 */
class VW454ValidationTest {

    private final MockGitHubPort gitHub = new MockGitHubPort();
    private final MockSlackNotificationPort slack = new MockSlackNotificationPort();

    // System Under Test (SUT)
    // We expect a component that orchestrates this workflow.
    // For TDD, we assume the class name based on domain naming conventions.
    private final ReportDefectWorkflow workflow = new ReportDefectWorkflow(gitHub, slack);

    @AfterEach
    void tearDown() {
        gitHub.reset();
        slack.reset();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectTitle = "VW-454: Validation Failure";
        String defectDescription = "Critical validation step failed in production.";
        String expectedGitHubUrl = "https://github.com/bank-of-z/core/issues/454";
        
        // Configure the mock GitHub to return a specific URL
        gitHub.setNextReturnUrl(expectedGitHubUrl);

        // Act
        workflow.report(defectTitle, defectDescription);

        // Assert
        // 1. Verify the issue was created in GitHub
        assertThat(gitHub.getCreatedIssues()).hasSize(1);
        var createdIssue = gitHub.getCreatedIssues().get(0);
        assertThat(createdIssue.title()).isEqualTo(defectTitle);
        assertThat(createdIssue.body()).isEqualTo(defectDescription);

        // 2. Verify the notification was sent to Slack
        assertThat(slack.getSentMessages()).hasSize(1);
        String slackBody = slack.getSentMessages().get(0);

        // 3. Verify the Regression Criteria: Slack body includes GitHub issue URL
        // This is the core validation for VW-454.
        assertThat(slackBody).contains(expectedGitHubUrl);
    }

    @Test
    void shouldHandleMultipleDefectsCorrectly() {
        // Arrange
        String url1 = "https://github.com/bank-of-z/core/issues/101";
        String url2 = "https://github.com/bank-of-z/core/issues/102";
        
        gitHub.setNextReturnUrl(url1);
        workflow.report("Defect 1", "Description 1");
        
        gitHub.setNextReturnUrl(url2);
        workflow.report("Defect 2", "Description 2");

        // Assert
        assertThat(slack.getSentMessages()).hasSize(2);
        assertThat(slack.getSentMessages().get(0)).contains(url1);
        assertThat(slack.getSentMessages().get(1)).contains(url2);
        
        // Verify no URL crossover
        assertThat(slack.getSentMessages().get(0)).doesNotContain(url2);
    }

    @Test
    void shouldThrowExceptionIfGitHubCreationFails() {
        // Arrange
        gitHub.setShouldFail(true);

        // Act & Assert
        assertThatThrownBy(() -> workflow.report("Fail Test", "This should fail"))
                .isInstanceOf(RuntimeException.class); // Or specific workflow exception
        
        // Verify no Slack message is sent if GitHub fails
        assertThat(slack.getSentMessages()).isEmpty();
    }
}
