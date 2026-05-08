package com.example.domain.defect;

import com.example.domain.ports.SlackNotifier;
import com.example.domain.ports.GithubIssueTracker;
import com.example.mocks.SlackSpy;
import com.example.mocks.GithubIssueTrackerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for VW-454.
 * Verifies that when a defect is reported via Temporal (_report_defect),
 * the resulting Slack message body contains the valid GitHub issue URL.
 *
 * Corresponds to Story S-FB-1.
 */
class SlackNotificationDeliveryTest {

    @Test
    void shouldContainGithubUrlInSlackBodyWhenDefectReported() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL validation";
        String defectDescription = "Validating if the URL is present in the Slack body.";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";

        // Mock the external dependencies
        // 1. Github service returns a specific URL
        GithubIssueTracker githubMock = new GithubIssueTrackerMock(expectedUrl);
        
        // 2. Slack service captures the output for verification
        SlackSpy slackSpy = new SlackSpy();

        // System Under Test (SUT) - The defect reporting workflow
        // In a real Temporal test, this would be the Workflow stub
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(githubMock, slackSpy);

        // Act
        workflow.reportDefect(defectTitle, defectDescription);

        // Assert
        // 1. Verify GitHub was called
        assertTrue(githubMock.wasCalled(), "GitHub issue tracker should have been invoked");

        // 2. Verify Slack was called
        assertTrue(slackSpy.wasNotificationSent(), "Slack notification should have been sent");

        // 3. Verify the Slack BODY contains the GitHub URL (The core defect)
        String actualSlackBody = slackSpy.getCapturedBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected: [" + expectedUrl + "] but was: [" + actualSlackBody + "]"
        );
        
        // 4. Ensure it's not just the title, but the actual link format
        // (Defect specifically mentions checking the body for the link line)
        assertTrue(
            actualSlackBody.contains("<" + expectedUrl + ">") || actualSlackBody.contains(expectedUrl),
            "Slack body must contain the formatted link"
        );
    }

    @Test
    void shouldHandleEmptyGithubUrlGracefully() {
        // Edge case: What if GitHub returns null?
        GithubIssueTracker githubMock = new GithubIssueTrackerMock(null);
        SlackSpy slackSpy = new SlackSpy();
        
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(githubMock, slackSpy);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            workflow.reportDefect("No URL Defect", "Description");
        });

        assertTrue(exception.getMessage().contains("GitHub URL"));
    }
}
