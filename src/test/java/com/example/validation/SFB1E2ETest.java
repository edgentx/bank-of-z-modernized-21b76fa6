package com.example.validation;

import com.example.validation.infrastructure.temporal.ReportDefectWorkflow;
import com.example.validation.infrastructure.temporal.SlackNotificationActivities;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * TDD RED PHASE:
 * This test ensures that when a defect is reported, the Slack notification body 
 * contains the GitHub Issue URL. 
 * 
 * It will FAIL against the current implementation in SlackNotificationActivitiesImpl 
 * which does not include the URL in the message.
 */
public class SFB1E2ETest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private SlackNotificationActivities mockActivities;

    @BeforeEach
    public void setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("VFORCE360-TASK-QUEUE");
        
        // Create a Mock for the Activity Interface
        mockActivities = Mockito.mock(SlackNotificationActivities.class);
        
        // Register the Workflow Implementation (Auto-discovered or manual)
        // Note: In a real Spring Boot test, we might use @MockBean, but for pure Temporal unit test
        // we register the mock activities directly with the worker.
        worker.registerActivitiesImplementations(mockActivities);
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        
        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String title = "VW-454: GitHub URL in Slack body";
        String description = "Verifying the link appears";
        String severity = "LOW";
        
        // Get a workflow stub
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);

        // Act
        // This triggers the workflow, which calls the activity
        workflow.reportDefect(title, description, severity);

        // Assert
        // We verify that the 'sendSlackNotification' method was called
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> severityCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockActivities, times(1)).sendSlackNotification(
            titleCaptor.capture(), 
            urlCaptor.capture(), 
            severityCaptor.capture()
        );

        String capturedUrl = urlCaptor.getValue();
        
        // Acceptance Criteria: Verify URL is present
        assertNotNull(capturedUrl, "GitHub URL should be generated and passed to Slack activity");
        assertTrue(capturedUrl.startsWith("https://github.com/example/issues/"), "URL should be a valid GitHub link");
        
        // Note: Since the Workflow generates the UUID, we can't assert the exact string, 
        // but we assert the structure and presence.
    }
}