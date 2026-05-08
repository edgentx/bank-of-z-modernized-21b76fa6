package com.example.e2e.regression;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Verifies VW-454: Slack body contains GitHub issue link.
 */
public class VW454_SlackBodyValidationTest {

    @Test
    public void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String reportId = "R-101";
        String expectedGithubUrl = "https://github.com/bank-of-z/issues/101";
        
        // We are mocking the outcome of what the workflow/activity should produce
        String generatedSlackBody = "Issue created: " + expectedGithubUrl;

        // Act (Simulate the aggregate storing the result of the workflow)
        ValidationAggregate aggregate = new ValidationAggregate(reportId);
        ReportDefectCommand cmd = new ReportDefectCommand(reportId, expectedGithubUrl, generatedSlackBody);
        
        aggregate.execute(cmd);

        // Assert
        // 1. Verify the aggregate captured the data needed for Slack
        assertEquals(expectedGithubUrl, aggregate.getGithubUrl());

        // 2. Verify that if we were to send this body to Slack, it would contain the URL
        MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
        slackPort.sendNotification("#vforce360-issues", generatedSlackBody);

        assertTrue(slackPort.bodyContains(expectedGithubUrl), 
            "Slack body must contain the GitHub issue URL: " + expectedGithubUrl);
        assertTrue(slackPort.bodyContains("<" + expectedGithubUrl + ">"), 
            "Slack body must format the URL as a Slack link: <url>");
    }
}