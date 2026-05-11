package com.example.domain.validation;

import com.example.domain.shared.ports.GitHubPort;
import com.example.domain.shared.ports.NotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Test for defect VW-454
 * 
 * This test verifies that when reporting a defect via the notification service,
 * the resulting Slack message includes a link to the GitHub issue that was created.
 * 
 * Expected behavior: Slack body includes GitHub issue URL
 * Actual behavior (defect): Slack body does not include GitHub issue URL
 */
@DisplayName("VW-454: Slack notifications should include GitHub issue URLs")
class SlackNotificationGitHubUrlTest {
    
    private NotificationPort notificationPort;
    private GitHubPort gitHubPort;
    private DefectReportService defectReportService;
    
    @BeforeEach
    void setUp() {
        notificationPort = new MockNotificationPort();
        gitHubPort = new MockGitHubPort();
        // The actual implementation doesn't exist yet - this will fail to compile
        // defectReportService = new DefectReportService(notificationPort, gitHubPort);
    }
    
    @Nested
    @DisplayName("When reporting a defect via _report_defect temporal workflow")
    class ReportDefectWorkflow {
        
        @Test
        @DisplayName("should send Slack message with GitHub issue URL")
        void testSlackMessageContainsGitHubUrl() {
            // Arrange
            String defectId = "VW-454";
            String title = "Validating VW-454 — GitHub URL in Slack body";
            String description = "Slack body includes GitHub issue: <url>";
            String channel = "#vforce360-issues";
            
            // Act
            boolean result = defectReportService.reportDefect(defectId, title, description, channel);
            
            // Assert
            assertTrue(result, "Defect report should succeed");
            
            MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
            assertTrue(
                mockNotification.messageContains(channel, "github.com"),
                "Slack message should contain GitHub URL"
            );
            assertTrue(
                mockNotification.messageContains(channel, "https://github.com"),
                "Slack message should contain full GitHub URL"
            );
        }
        
        @Test
        @DisplayName("should include GitHub issue link in Slack message body")
        void testGitHubLinkFormatInSlackMessage() {
            // Arrange
            String defectId = "VW-454";
            String title = "Validating VW-454 — GitHub URL in Slack body";
            String description = "Defect in Slack notification";
            String channel = "#vforce360-issues";
            
            // Act
            boolean result = defectReportService.reportDefect(defectId, title, description, channel);
            
            // Assert
            assertTrue(result, "Defect report should succeed");
            
            MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
            MockNotificationPort.SlackMessage lastMessage = mockNotification.getLastMessage();
            
            assertNotNull(lastMessage, "A message should have been sent");
            assertEquals(channel, lastMessage.channel, "Message should go to the correct channel");
            
            String message = lastMessage.message;
            assertTrue(
                message.contains("<https://github.com") || message.contains("https://github.com"),
                "Message should contain GitHub URL"
            );
            assertTrue(
                message.contains("issue") || message.contains("Issue"),
                "Message should mention 'issue'"
            );
        }
        
        @Test
        @DisplayName("should create GitHub issue before sending Slack notification")
        void testGitHubIssueCreatedBeforeSlackNotification() {
            // Arrange
            String defectId = "VW-454";
            String title = "Validating VW-454 — GitHub URL in Slack body";
            String description = "Defect in Slack notification";
            String channel = "#vforce360-issues";
            
            // Act
            boolean result = defectReportService.reportDefect(defectId, title, description, channel);
            
            // Assert
            assertTrue(result, "Defect report should succeed");
            
            MockGitHubPort mockGitHub = (MockGitHubPort) gitHubPort;
            assertEquals(1, mockGitHub.getIssueCount(), "GitHub issue should be created");
            
            MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
            assertTrue(
                mockNotification.messageContains(channel, mockGitHub.getIssueUrl("1")),
                "Slack message should contain the URL of the created GitHub issue"
            );
        }
        
        @Test
        @DisplayName("should handle GitHub API failure gracefully")
        void testGitHubApiFailure() {
            // Arrange
            MockGitHubPort mockGitHub = (MockGitHubPort) gitHubPort;
            mockGitHub.setShouldFail(true);
            
            String defectId = "VW-454";
            String title = "Validating VW-454 — GitHub URL in Slack body";
            String description = "Defect in Slack notification";
            String channel = "#vforce360-issues";
            
            // Act
            boolean result = defectReportService.reportDefect(defectId, title, description, channel);
            
            // Assert
            assertFalse(result, "Defect report should fail when GitHub API fails");
            
            MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
            assertTrue(
                mockNotification.getSentMessages().isEmpty(),
                "No Slack message should be sent when GitHub issue creation fails"
            );
        }
        
        @Test
        @DisplayName("should handle Slack API failure gracefully")
        void testSlackApiFailure() {
            // Arrange
            MockNotificationPort mockNotification = (MockNotificationPort) notificationPort;
            mockNotification.setShouldFail(true);
            
            String defectId = "VW-454";
            String title = "Validating VW-454 — GitHub URL in Slack body";
            String description = "Defect in Slack notification";
            String channel = "#vforce360-issues";
            
            // Act
            boolean result = defectReportService.reportDefect(defectId, title, description, channel);
            
            // Assert
            assertFalse(result, "Defect report should fail when Slack API fails");
            
            // GitHub issue should still be created
            MockGitHubPort mockGitHub = (MockGitHubPort) gitHubPort;
            assertEquals(1, mockGitHub.getIssueCount(), "GitHub issue should be created even if Slack fails");
        }
    }
    
    /**
     * This is the service class that we're testing.
     * In the actual implementation, this would be in src/main/java,
     * but for the red phase of TDD, we define it here to show what we're testing.
     * This class doesn't exist yet, so the test will fail to compile.
     */
    static class DefectReportService {
        private final NotificationPort notificationPort;
        private final GitHubPort gitHubPort;
        
        public DefectReportService(NotificationPort notificationPort, GitHubPort gitHubPort) {
            this.notificationPort = notificationPort;
            this.gitHubPort = gitHubPort;
        }
        
        public boolean reportDefect(String defectId, String title, String description, String channel) {
            // Implementation to be done in green phase
            return false;
        }
    }
}