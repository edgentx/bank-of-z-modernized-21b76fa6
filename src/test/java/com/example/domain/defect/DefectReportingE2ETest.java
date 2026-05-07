package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.ports.GitHubIssueTracker;
import com.example.domain.defect.ports.NotificationService;
import com.example.mocks.MockGitHubIssueTracker;
import com.example.mocks.MockNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * Verifies that when a defect is reported, the resulting notification contains
 * the link to the created GitHub issue.
 *
 * Corresponds to Story ID: S-FB-1
 */
public class DefectReportingE2ETest {

    private MockGitHubIssueTracker mockGitHub;
    private MockNotificationService mockSlack;
    private DefectReportingService service; // System Under Test

    @BeforeEach
    public void setUp() {
        mockGitHub = new MockGitHubIssueTracker();
        mockSlack = new MockNotificationService();
        // In a real Spring Boot app, this would be instantiated by the context.
        // Here we manually inject mocks for the unit test isolation.
        service = new DefectReportingService(mockGitHub, mockSlack);
    }

    @AfterEach
    public void tearDown() {
        mockGitHub.reset();
        mockSlack.reset();
    }

    /**
     * Acceptance Criterion: "The validation no longer exhibits the reported behavior"
     * Regression test for VW-454.
     */
    @Test
    public void testReportDefect_ShouldIncludeGitHubLinkInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId,
            "GitHub URL missing",
            "Slack body does not contain the link",
            "LOW",
            "validation"
        );

        // Act
        service.processDefect(cmd);

        // Assert
        // 1. Verify GitHub was actually called
        assertEquals(1, mockGitHub.requests.size(), "GitHub issue creation should be attempted once");
        
        // 2. Get the URL that GitHub "returned"
        String expectedUrl = "https://github.com/fake-project/issues/1";
        
        // 3. Verify Slack was called
        assertEquals(1, mockSlack.sentMessages.size(), "Slack notification should be sent once");
        
        // 4. CRITICAL ASSERTION for VW-454
        String slackBody = mockSlack.sentMessages.get(0);
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL.\nExpected: " + expectedUrl + "\nActual: " + slackBody
        );
    }

    @Test
    public void testReportDefect_ShouldMapCommandFieldsCorrectly() {
        // Arrange
        ReportDefectCommand cmd = new ReportDefectCommand(
            "S-FB-1",
            "Fix Validation",
            "Implement validation",
            "MEDIUM",
            "domain"
        );

        // Act
        service.processDefect(cmd);

        // Assert
        MockGitHubIssueTracker.IssueRequest ghRequest = mockGitHub.requests.get(0);
        assertTrue(ghRequest.title().contains(cmd.title()) || ghRequest.title().contains(cmd.defectId()), 
            "GitHub Title should be derived from the command");
        
        String slackBody = mockSlack.sentMessages.get(0);
        assertTrue(slackBody.contains("Fix Validation"), "Slack body should contain context");
    }
}
