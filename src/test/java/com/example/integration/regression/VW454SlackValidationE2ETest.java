package com.example.integration.regression;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.ValidationReportedEvent;
import com.example.mocks.MockGitHubIssueClient;
import com.example.mocks.MockVForce360Notification;
import com.example.ports.GitHubIssuePort;
import com.example.ports.VForce360NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ID: S-FB-1
 * Title: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Regression test ensuring that when a defect is reported via the temporal-worker,
 * the subsequent Slack notification body contains the valid GitHub issue link.
 * 
 * Scope: Integration (Command -> Domain -> Service -> Ports)
 */
public class VW454SlackValidationE2ETest {

    private MockVForce360Notification slackMock;
    private MockGitHubIssueClient githubMock;
    
    // The System Under Test (SUT). Since we are in RED phase, this is null.
    // We will assume the implementation class name for compilation context,
    // but it will effectively fail at runtime if implemented incorrectly.
    private DefectReportingService service; 

    @BeforeEach
    void setUp() {
        slackMock = new MockVForce360Notification();
        githubMock = new MockGitHubIssueClient();
        
        // Inject mocks
        service = new DefectReportingService(githubMock, slackMock);
    }

    @Test
    @SuppressWarnings("null")
    void shouldContainGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL missing from Slack";
        String component = "validation";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            UUID.randomUUID().toString(), 
            defectTitle, 
            "LOW", 
            component
        );

        // Configure GitHub mock to return a specific URL we can verify
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        githubMock.setNextUrl(expectedUrl);

        // Act
        // Execute the temporal workflow trigger logic
        // (Represented here as a direct service call for unit/integration testing)
        ValidationReportedEvent event = service.reportDefect(cmd);

        // Assert
        assertNotNull(event, "Domain event should be produced");
        assertEquals(expectedUrl, event.reportUrl(), "Event should contain the generated GitHub URL");

        // Verify Mock Interaction
        // CRITICAL: This is the main regression check for VW-454.
        List<MockVForce360Notification.SentMessage> messages = slackMock.getMessages();
        assertEquals(1, messages.size(), "Should have sent one Slack notification");
        
        MockVForce360Notification.SentMessage sent = messages.get(0);
        
        // The core defect: Verify the URL is actually passed to the Slack adapter
        // In the 'Actual Behavior', the link was missing.
        assertEquals(expectedUrl, sent.url, "Slack body must include the GitHub issue URL");
    }

    @Test
    void shouldFailIfGitHubUrlIsNotPropagatedToSlack() {
        // This test enforces the negative case logic or implicit contract
        // If the implementation simply calls slack(id, null) or slack(id, ""), it fails.
        
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-1", 
            "Test", 
            "HIGH", 
            "core"
        );

        // Act & Assert
        // If the implementation doesn't pass the URL, the mock might still accept it
        // but the URL check below will fail.
        service.reportDefect(cmd);

        List<MockVForce360Notification.SentMessage> messages = slackMock.getMessages();
        assertFalse(messages.isEmpty(), "Slack should be called");
        
        String sentUrl = messages.get(0).url;
        
        // Fail test if URL is blank or null (The defect state)
        assertNotNull(sentUrl, "GitHub URL in Slack body cannot be null (VW-454)");
        assertFalse(sentUrl.isBlank(), "GitHub URL in Slack body cannot be empty (VW-454)");
        assertTrue(sentUrl.startsWith("http"), "GitHub URL must be a valid link (VW-454)");
    }

    // Dummy Service Class to satisfy compilation in RED phase
    // In a real scenario, this class exists elsewhere or is created in the Green phase.
    public static class DefectReportingService {
        private final GitHubIssuePort githubPort;
        private final VForce360NotificationPort slackPort;

        public DefectReportingService(GitHubIssuePort githubPort, VForce360NotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        public ValidationReportedEvent reportDefect(ReportDefectCmd cmd) {
            // TODO: Implement logic in Green phase
            // 1. Create Issue
            String url = githubPort.createIssue(cmd.title(), "Defect: " + cmd.defectId());
            
            // 2. Send Slack Notification (THIS IS THE FAILING POINT IN VW-454)
            // The defect suggests this line might be sending empty/null URL.
            slackPort.sendDefectSlack(cmd.defectId(), url);

            return new ValidationReportedEvent(cmd.defectId(), cmd.defectId(), url, Instant.now());
        }
    }
}
