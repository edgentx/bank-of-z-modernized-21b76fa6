package com.example.domain.notification;

import com.example.mocks.MockGitHubIssueTrackerAdapter;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD RED Phase Test for Story VW-454.
 * Verifies that when a defect is reported (workflow step),
 * the resulting Slack notification body contains the GitHub URL.
 */
class VW454_SlackValidationTest {

    private MockSlackNotificationAdapter mockSlack;
    private MockGitHubIssueTrackerAdapter mockGitHub;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationAdapter();
        mockGitHub = new MockGitHubIssueTrackerAdapter();
        
        // We inject the mocks into the service under test.
        // This class will be created in the Green phase to make the test pass.
        service = new DefectReportingService(mockSlack, mockGitHub);
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL missing from Slack body";
        String defectDescription = "Severity: LOW\nComponent: validation";
        String targetChannel = "#vforce360-issues";

        // Act
        service.reportDefect(defectTitle, defectDescription, targetChannel);

        // Assert
        // 1. Verify GitHub was called
        assertEquals(1, mockGitHub.getCallCount());
        String expectedGitHubUrl = "https://github.com/example/bank/issues/1";

        // 2. Verify Slack was called
        assertEquals(1, mockSlack.postedMessages.size());
        
        // 3. Verify the Link is in the body (The core acceptance criteria)
        MockSlackNotificationAdapter.Message slackMsg = mockSlack.postedMessages.get(0);
        assertTrue(
            slackMsg.content().contains(expectedGitHubUrl),
            "Expected Slack body to contain GitHub URL [" + expectedGitHubUrl + "], but got: " + slackMsg.content()
        );
        
        // 4. Verify it was sent to the right place
        assertEquals(targetChannel, slackMsg.channel());
    }

    @Test
    void shouldFormatBodyWithUrlPrefix() {
        // Arrange
        String title = "Defect X";
        String desc = "Details";
        String expectedUrl = "http://github.com/repo/issue/42";
        mockGitHub.setNextMockUrl(expectedUrl); 

        // Act
        service.reportDefect(title, desc, "#dev-ops");

        // Assert
        String content = mockSlack.postedMessages.get(0).content();
        // Looking for the format "GitHub issue: <url>"
        assertTrue(content.contains("GitHub issue: " + expectedUrl));
    }
}
