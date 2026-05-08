package com.example.e2e.regression;

import com.example.domain.notification.NotificationService;
import com.example.domain.notification.ReportDefectCommand;
import com.example.mocks.MockSlackPort;
import com.example.mocks.MockGitHubPort;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1.
 * Ensures that when a defect is reported, the resulting Slack message
 * contains a link to the GitHub issue created.
 * 
 * Corresponds to VW-454.
 */
public class SFb1RegressionTest {

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        MockSlackPort mockSlack = new MockSlackPort();
        MockGitHubPort mockGitHub = new MockGitHubPort();
        NotificationService service = new NotificationService(mockSlack, mockGitHub);

        String title = "VW-454 Regression Check";
        String description = "Ensure URL is present in Slack body";
        ReportDefectCommand cmd = new ReportDefectCommand(title, description);

        // Act
        service.handleReportDefect(cmd);

        // Assert
        String sentMessage = mockSlack.getLastMessage();
        assertNotNull(sentMessage, "Message should have been sent to Slack");
        
        String expectedUrl = mockGitHub.getLastCreatedUrl();
        assertTrue(
            sentMessage.contains(expectedUrl), 
            "Regression check failed: Slack body does not contain GitHub URL.\nMessage was: " + sentMessage
        );
    }
}