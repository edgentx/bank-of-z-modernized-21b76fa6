package com.example.workflows;

import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Tests for Story S-FB-1.
 * Validating VW-454 — GitHub URL in Slack body (end-to-end).
 *
 * These tests verify that when a defect is reported via the Temporal workflow,
 * the resulting Slack notification contains the valid GitHub issue URL.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;

    @Mock
    private ReportDefectActivity activitiesMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflowImpl.class, () -> new ReportDefectWorkflowImpl());
        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_SlackBodyContainsGitHubUrl() {
        // ARRANGE
        String title = "VW-454: Missing GitHub URL";
        String description = "The Slack notification does not contain the GitHub link.";
        String expectedId = "d-12345";
        String expectedGithubUrl = "https://github.com/example-org/bank-of-z/issues/454";

        // Define mock behavior to simulate the workflow steps
        when(activitiesMock.generateId()).thenReturn(expectedId);
        doNothing().when(activitiesMock).saveDefect(eq(expectedId), eq(title), eq(description));
        when(activitiesMock.createGitHubIssue(title, description)).thenReturn(expectedGithubUrl);

        // ACT
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        workflow.reportDefect(title, description);

        // ASSERT - Acceptance Criteria: Validation no longer exhibits reported behavior
        // Verify that the Slack notification was triggered
        verify(activitiesMock, times(1)).notifySlack(eq(expectedId), anyString());

        // Capture the actual URL passed to Slack
        java.lang.reflect.ArgumentCaptor<String> urlCaptor = java.lang.reflect.ArgumentCaptor.forClass(String.class);
        verify(activitiesMock).notifySlack(eq(expectedId), urlCaptor.capture());
        
        String actualUrl = urlCaptor.getValue();
        
        // ASSERT - Expected Behavior: Slack body includes GitHub issue: <url>
        assertEquals(expectedGithubUrl, actualUrl, "Slack body must contain the exact GitHub issue URL");
        assertTrue(actualUrl.startsWith("https://github.com/"), "URL must be a valid GitHub link");
    }

    @Test
    public void testReportDefect_GitHubUrlIsNotNull() {
        // ARRANGE
        String title = "Null Check Test";
        String description = "Ensure URL is not null";
        String expectedGithubUrl = "https://github.com/example-org/bank-of-z/issues/1";

        when(activitiesMock.generateId()).thenReturn("d-1");
        doNothing().when(activitiesMock).saveDefect(anyString(), anyString(), anyString());
        when(activitiesMock.createGitHubIssue(title, description)).thenReturn(expectedGithubUrl);

        // ACT
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        workflow.reportDefect(title, description);

        // ASSERT
        java.lang.reflect.ArgumentCaptor<String> urlCaptor = java.lang.reflect.ArgumentCaptor.forClass(String.class);
        verify(activitiesMock).notifySlack(anyString(), urlCaptor.capture());

        String actualUrl = urlCaptor.getValue();
        assertTrue(actualUrl != null && !actualUrl.isBlank(), "GitHub URL in Slack body cannot be null or blank");
    }
}
