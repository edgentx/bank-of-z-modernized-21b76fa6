package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.port.SlackNotifier;
import com.example.domain.defect.port.GitHubIssueTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Verify defect reporting workflow generates correct GitHub URL and Slack notification.
 *
 * Story: VW-454 — GitHub URL in Slack body (end-to-end)
 * Criteria: Validation no longer exhibits broken behavior; Regression test added.
 */
public class ReportDefectCommandTest {

    @Mock
    private GitHubIssueTracker mockGitHub;

    @Mock
    private SlackNotifier mockSlack;

    private DefectReportOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orchestrator = new DefectReportOrchestrator(mockGitHub, mockSlack);
    }

    @Test
    void whenReportDefectCommandIsExecuted_shouldCreateGitHubIssueAndPostToSlackWithLink() {
        // Given
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String defectTitle = "Defect: Validating VW-454";
        String defectDescription = "Slack body should contain GitHub issue: <url>";
        String expectedIssueId = "GH-123";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + expectedIssueId;

        // We define the expected URL format strictly to catch regressions where the URL might be malformed
        String expectedSlackBody = "Defect Reported: " + defectTitle + "\nIssue: " + expectedUrl;

        // Configure GitHub mock to return a realistic URL structure
        when(mockGitHub.createIssue(eq(defectTitle), anyString()))
            .thenReturn(new GitHubIssueTracker.IssueDetails(expectedIssueId, expectedUrl));

        ReportDefectCmd cmd = new ReportDefectCmd(projectId, defectTitle, defectDescription);

        // When
        List<DefectReportedEvent> events = orchestrator.execute(cmd);

        // Then
        // 1. Verify an event was produced
        assertNotNull(events);
        assertEquals(1, events.size());

        // 2. Verify the internal state of the event
        DefectReportedEvent event = events.get(0);
        assertEquals(projectId, event.aggregateId());
        assertEquals(expectedUrl, event.githubIssueUrl());
        assertNotNull(event.occurredAt());

        // 3. Verify External System 1 (GitHub) was called correctly
        verify(mockGitHub).createIssue(defectTitle, defectDescription);

        // 4. Verify External System 2 (Slack) received the correct link in the body
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).sendNotification(slackBodyCaptor.capture());

        String actualSlackBody = slackBodyCaptor.getValue();

        // CRITICAL ASSERTION: This validates the VW-454 fix.
        // The Slack body MUST contain the specific GitHub URL returned by the mock.
        assertTrue(actualSlackBody.contains(expectedUrl),
            "Slack body must contain the specific GitHub Issue URL. Received: " + actualSlackBody);
    }
}
