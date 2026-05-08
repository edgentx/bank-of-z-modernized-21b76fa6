package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.InMemoryGitHubAdapter;
import com.example.mocks.InMemorySlackAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Validation for VW-454.
 *
 * Story: Verify that when _report_defect is triggered,
 * the Slack message body contains a link to the created GitHub issue.
 */
public class DefectReportValidationTest {

    private GitHubPort mockGitHub;
    private SlackNotificationPort mockSlack;

    // This is the class we are driving the creation of.
    // It will likely be a Spring Service or Temporal Activity implementation.
    private DefectReportWorkflowService workflowService;

    @BeforeEach
    void setUp() {
        // In a real Spring Boot test, these would be @MockBeans or configured beans.
        // Here we use simple in-memory adapters to control behavior.
        mockGitHub = new InMemoryGitHubAdapter();
        mockSlack = new InMemorySlackAdapter();

        // Inject dependencies into the System Under Test (SUT)
        workflowService = new DefectReportWorkflowService(mockGitHub, mockSlack);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // 1. Setup: Configure GitHub to return a specific URL
        String expectedTitle = "VW-454: GitHub URL in Slack body";
        String expectedIssueUrl = "https://github.com/example/bank-of-z/issues/454";
        
        // Configure the mock to return this specific URL when createIssue is called
        ((InMemoryGitHubAdapter) mockGitHub).setNextIssueUrl(expectedIssueUrl);

        // 2. Act: Execute the workflow command
        // Note: DefectReportCmd is a placeholder for the actual command object structure
        workflowService.reportDefect(expectedTitle, "Defect details...");

        // 3. Assert: Verify Slack received the URL in the body
        InMemorySlackAdapter slack = (InMemorySlackAdapter) mockSlack;
        
        // Check that a message was actually sent
        assertTrue(slack.wasCalled(), "Slack should have received a notification");
        
        // Check the content
        String lastMessageBody = slack.getLastMessageBody();
        assertNotNull(lastMessageBody, "Slack body should not be null");
        
        // The critical assertion: Does the body contain the GitHub link?
        assertTrue(
            lastMessageBody.contains(expectedIssueUrl), 
            "Slack body must contain the GitHub Issue URL. Expected: " + expectedIssueUrl + " but got: " + lastMessageBody
        );
    }

    @Test
    void testReportDefect_ShouldFailValidationIfUrlMissingFromSlack() {
        // Regression test to ensure we don't ship a version that forgets the link.
        // We simulate a 'broken' GitHub adapter that returns empty or null, or a broken workflow.
        // However, in TDD Red, we assert the POSITIVE case first (above).
        // This test verifies the bounds of the contract.
        
        ((InMemoryGitHubAdapter) mockGitHub).setNextIssueUrl(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            workflowService.reportDefect("Null URL Test", "Testing...");
        });

        assertTrue(exception.getMessage().contains("GitHub URL generation failed"));
    }
}