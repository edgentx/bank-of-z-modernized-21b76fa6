package com.example.domain.validation;

import com.example.domain.validation.model.Vw454ValidatedEvent;
import com.example.domain.validation.port.SlackNotificationPort;
import com.example.domain.validation.port.GitHubIssuePort;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockGitHubIssueAdapter;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Specification for VW-454 Validation.
 * 
 * Context:
 * 1. Temporal workflow triggers defect report (_report_defect).
 * 2. System must validate outcome.
 * 3. Validation expects a Slack notification containing a GitHub Issue URL.
 * 
 * Acceptance Criteria:
 * - The validation no longer exhibits the reported behavior (link missing).
 * - Regression test covers end-to-end scenario.
 */
public class Vw454ValidationTest {

    /**
     * AC: Regression test added to e2e/regression/ covering this scenario.
     * Scenario: Triggering a defect report results in a validation event
     * that confirms the presence of the GitHub link in the Slack body.
     */
    @Test
    public void testValidationConfirmsGitHubLinkInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedGitHubUrl = "https://github.com/bank-of-z/z-legacy/issues/454";
        
        // Use Mock Adapters for external dependencies (Slack API, GitHub API)
        SlackNotificationPort mockSlack = new MockSlackNotificationAdapter();
        GitHubIssuePort mockGitHub = new MockGitHubIssueAdapter();

        // We expect the GitHub adapter to return this URL
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(expectedGitHubUrl);

        // The Slack adapter should be ready to capture the body
        // (In real implementation, this is async, but we mock the interaction)

        // Act
        // We simulate the Command that Temporal would trigger
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Validation Issue", "Slack body missing link");
        
        ValidationAggregate aggregate = new ValidationAggregate(defectId, mockSlack, mockGitHub);
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. We expect a validation event to be produced
        assertNotNull(events, "Validation should produce an event");
        assertEquals(1, events.size(), "Should produce exactly one validation event");
        assertTrue(events.get(0) instanceof Vw454ValidatedEvent, "Event should be Vw454ValidatedEvent");

        Vw454ValidatedEvent validatedEvent = (Vw454ValidatedEvent) events.get(0);
        
        // 2. CRITICAL ASSERTION: The validation logic must confirm the URL is in the Slack body
        // If the URL is not in the body, the status should be FAILED (or exception thrown), not VALIDATED.
        // For TDD Red phase, we assert the state we WANT to see.
        assertEquals("VALIDATED", validatedEvent.status(), 
            "Validation should pass only if GitHub URL is present in Slack body");
        
        // Verify the interactions
        verify(mockSlack).sendNotification(argThat(body -> body.contains(expectedGitHubUrl)));
    }

    /**
     * Negative Test: Ensures validation FAILS if URL is missing.
     * This covers the "Actual Behavior" noted in the defect (link missing).
     */
    @Test
    public void testValidationFailsIfLinkIsMissing() {
        // Arrange
        String defectId = "VW-454-Missing";
        
        SlackNotificationPort mockSlack = mock(SlackNotificationPort.class);
        GitHubIssuePort mockGitHub = mock(GitHubIssuePort.class);

        // Slack sends a message, but we simulate the GitHub URL being absent or null
        // Mocking the internal verification state
        
        ValidationAggregate aggregate = new ValidationAggregate(defectId, mockSlack, mockGitHub);
        
        // Act & Assert
        // The aggregate should detect the missing link and throw an exception or emit a failed event
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Defect", "Description");
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub URL not found in Slack body"));
    }
}
