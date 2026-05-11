package com.example.domain.validation;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Validates that the GitHub issue URL is present in the Slack notification body.
 * 
 * Corresponds to Feature: S-FB-1
 */
class DefectReportSlackLinkTest {

    private MockSlackNotificationPort slackMock;
    private MockGitHubIssuePort gitHubMock;
    
    // The class under test (assumed to be injected or instantiated)
    // In the actual implementation, this would be the Service or Workflow class
    // responsible for the '_report_defect' logic.
    private DefectReportingService service; 

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackNotificationPort();
        gitHubMock = new MockGitHubIssuePort();
        
        // We inject the mocks into the service constructor.
        // This test class assumes the existence of 'DefectReportingService' 
        // which handles the business logic described in the story.
        // Since this is TDD Red phase, this class might not exist yet.
        service = new DefectReportingService(slackMock, gitHubMock);
    }

    @Test
    void testReportDefect_includesGitHubLinkInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example-org/repo/issues/VW-454";
        String channel = "#vforce360-issues";
        
        // Act
        // Triggering the equivalent of '_report_defect via temporal-worker exec'
        service.reportDefect(defectId, channel);

        // Assert
        // 1. Verify Slack was called
        assertEquals(1, slackMock.getCalls().size(), "Slack should have been called once");

        // 2. Verify correct channel
        MockSlackNotificationPort.Call call = slackMock.getCalls().get(0);
        assertEquals(channel, call.channel, "Should post to the correct channel");

        // 3. Verify the body contains the GitHub URL (The Core Fix)
        assertNotNull(call.body, "Slack body should not be null");
        assertTrue(
            call.body.contains(expectedUrl), 
            "Slack body must contain the full GitHub issue URL. Expected [" + expectedUrl + "] in body [" + call.body + "]"
        );
    }

    @Test
    void testReportDefect_handlesNullIssueId() {
        // Arrange
        String defectId = null;
        String channel = "#vforce360-issues";

        // Act & Assert
        // The service should handle this gracefully or throw a specific domain exception.
        // We expect a failure if the ID is null, but we primarily want to ensure it doesn't
        // send a malformed message without a URL.
        assertThrows(IllegalArgumentException.class, () -> {
            service.reportDefect(defectId, channel);
        });
        
        // Verify no Slack message was sent for invalid data
        assertTrue(slackMock.getCalls().isEmpty(), "Slack should not be called for null defect ID");
    }

    @Test
    void testReportDefect_generatesCorrectUrlFormat() {
        // Arrange
        String defectId = "VW-999";
        // Explicitly setting the mock to return a specific format to verify parsing logic
        gitHubMock.setBaseUrl("https://github.com/my-bank/issues/");
        String expectedUrl = "https://github.com/my-bank/issues/VW-999";
        
        // Act
        service.reportDefect(defectId, "#random-channel");

        // Assert
        String body = slackMock.getCalls().get(0).body;
        assertTrue(body.contains(expectedUrl), "Body must contain the correctly formatted URL");
    }
}
