package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHubIssueAdapter;
import com.example.mocks.MockSlackNotificationAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Defect Reporting (VW-454).
 * 
 * Scenario:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior:
 * - GitHub issue is created.
 * - Slack body includes GitHub issue: <url>
 */
class DefectAggregateTest {

    private MockGitHubIssueAdapter gitHubAdapter;
    private MockSlackNotificationAdapter slackAdapter;
    private DefectAggregate aggregate;

    @BeforeEach
    void setUp() {
        gitHubAdapter = new MockGitHubIssueAdapter();
        slackAdapter = new MockSlackNotificationAdapter();
        // Assuming constructor or setter injection for ports in the aggregate
        aggregate = new DefectAggregate("test-defect-id", gitHubAdapter, slackAdapter);
    }

    @Test
    void whenReportDefectCommandExecuted_thenGitHubIssueIsCreated() {
        // Given
        String title = "VW-454: GitHub URL in Slack body";
        String description = "The defect report failed to include the GitHub URL in the Slack notification.";
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", title, description);

        // When
        aggregate.execute(cmd);

        // Then
        assertTrue(gitHubAdapter.hasCreatedIssue(title), "GitHub adapter should have received the create issue call");
    }

    @Test
    void whenReportDefectCommandExecuted_thenSlackNotificationIsSent() {
        // Given
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", "Title", "Description");
        gitHubAdapter.setMockUrl("https://github.com/example/issues/454");

        // When
        aggregate.execute(cmd);

        // Then
        assertNotNull(slackAdapter.getLastMessage(), "Slack should have received a notification");
        assertEquals("#vforce360-issues", slackAdapter.getLastMessage().channel, "Notification should go to the correct channel");
    }

    @Test
    void whenReportDefectCommandExecuted_thenSlackBodyContainsGitHubUrl() {
        // Given
        String expectedUrl = "https://github.com/example/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", "Title", "Description");
        gitHubAdapter.setMockUrl(expectedUrl);

        // When
        aggregate.execute(cmd);

        // Then
        MockSlackNotificationAdapter.Message msg = slackAdapter.getLastMessage();
        assertNotNull(msg);
        // AC: "Slack body includes GitHub issue: <url>"
        assertTrue(msg.text.contains(expectedUrl), 
            "Slack body must contain the actual GitHub URL. Got: " + msg.text);
        
        // Verification of exact format implied by the defect report expectation
        assertTrue(msg.text.contains("GitHub issue:"), 
            "Slack body should indicate the context (GitHub issue)");
    }

    @Test
    void whenInvalidCommandIsExecuted_thenThrowsUnknownCommandException() {
        // Given
        String invalidCmd = "NotACommand";

        // Then/When
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(invalidCmd));
    }

    @Test
    void whenGitHubFails_thenSlackIsNotCalled() {
        // Given
        gitHubAdapter.setShouldFail(true);
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", "Title", "Description");

        // When/Then
        assertThrows(RuntimeException.class, () -> aggregate.execute(cmd));
        assertNull(slackAdapter.getLastMessage(), "Slack should not be notified if GitHub creation fails");
    }

    @Test
    void whenSlackFails_thenEventIsStillRecorded() {
        // Given
        slackAdapter.setShouldFail(true);
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", "Title", "Description");

        // When/Then
        // We expect the aggregate to emit the event, but the adapter failure might be handled gracefully or throw
        // For this test, we verify the GitHub link was generated first.
        assertDoesNotThrow(() -> aggregate.execute(cmd));
        assertTrue(gitHubAdapter.hasCreatedIssue("Title"));
    }
}
