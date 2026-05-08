package com.example.e2e.regression;

import com.example.mocks.InMemoryValidationRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import com.example.domain.validation.repository.ValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454: Verify that when a defect is reported via Temporal,
 * the Slack notification body contains the GitHub issue URL.
 * 
 * This represents the 'Red Phase' of TDD: the test describes the expected behavior,
 * and will fail until the implementation is complete.
 */
public class Vw454SlackUrlRegressionTest {

    private ValidationRepository validationRepository;
    private MockNotificationPort mockNotificationPort;
    private MockGitHubPort mockGitHubPort;
    private DefectReportWorkflowSUT workflow;

    @BeforeEach
    void setUp() {
        validationRepository = new InMemoryValidationRepository();
        mockNotificationPort = new MockNotificationPort();
        mockGitHubPort = new MockGitHubPort("https://github.com/fake-org/repo");
        
        // System Under Test (SUT) - The Workflow/Service that orchestrates reporting
        workflow = new DefectReportWorkflowSUT(
            validationRepository, 
            mockNotificationPort, 
            mockGitHubPort
        );
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_whenReportDefectWorkflowCompletes() {
        // Arrange
        String defectTitle = "Defect VW-454: Missing URL in Slack";
        String defectDescription = "The validation logic does not append the GitHub link.";
        
        // Act - Simulate the Temporal workflow execution
        workflow.reportDefect(defectTitle, defectDescription);
        
        // Assert - Verify the expected behavior (Acceptance Criteria)
        // 1. Verify Notification was called
        assertFalse(mockNotificationPort.messages.isEmpty(), "Notification should have been sent");
        
        // 2. Verify the Body contains a URL
        MockNotificationPort.Message message = mockNotificationPort.messages.get(0);
        String body = message.body();
        
        // Expected: The body should start with http/https (GitHub URL)
        // This acts as a regression test for the defect where the URL was missing.
        assertTrue(
            body.startsWith("http"), 
            "Slack body should include GitHub issue URL. Actual body: " + body
        );
        
        // 3. Specifically check for the mock URL structure to be sure it's the GitHub link
        assertTrue(
            body.contains("github.com/fake-org/repo/issue/"),
            "Slack body should contain the specific GitHub issue URL generated."
        );
    }

    /**
     * SUT (System Under Test) Stub.
     * In the actual codebase, this would be a Spring Bean or a Temporal Workflow implementation.
     * Here we implement just enough to make the code compilable and demonstrate the test intention.
     */
    public static class DefectReportWorkflowSUT {
        private final ValidationRepository repo;
        private final NotificationPort notifier;
        private final GitHubPort github;

        public DefectReportWorkflowSUT(ValidationRepository repo, NotificationPort notifier, GitHubPort github) {
            this.repo = repo;
            this.notifier = notifier;
            this.github = github;
        }

        public void reportDefect(String title, String description) {
            // Step 1: Create GitHub Issue
            String url = github.createIssue(title, description);

            // Step 2: Send Notification
            // The Defect VW-454 implies the URL might be missing here.
            // The regression test ensures 'url' is actually present in the body.
            
            // INTENTIONAL BUG (to simulate 'Red' or 'Actual Behavior' if impl was missing):
            // If we were simulating the bug, we might do: notifier.sendNotification(title, description);
            // But since we are writing the test for the *fix*, we assume the implementation 
            // SHOULD append the url. We will let the test fail if the impl is wrong.
            
            String body = "Issue created: " + url; 
            notifier.sendNotification(title, body);
        }
    }
}
