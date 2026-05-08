package com.example.e2e;

import com.example.mocks.InMemoryValidationRepository;
import com.example.workflow.DefectReportingWorkflow;
import com.example.workflows.ReportDefectActivity;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkflowImplementationOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * E2E Regression Test for Story S-FB-1 (VW-454)
 * Verifies that when a defect is reported, the resulting Slack body
 * contains the correct GitHub issue URL.
 */
public class VW454ValidationWorkflowE2ETest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private ReportDefectActivity mockActivity;
    private InMemoryValidationRepository repository;

    @BeforeEach
    public void setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance();
        mockActivity = mock(ReportDefectActivity.class);
        repository = new InMemoryValidationRepository();

        worker = testEnvironment.newWorker("DEFECT_REPORTING_TASK_QUEUE");
        
        // Register Workflow with mock dependencies
        com.example.workflow.DefectReportingWorkflowImpl workflowImpl = 
            new com.example.workflow.DefectReportingWorkflowImpl(repository, mockActivity);

        worker.registerWorkflowImplementationFactory(
            DefectReportingWorkflow.class,
            () -> workflowImpl
        );
        
        // Register Activity mock
        worker.registerActivitiesImplementations(mockActivity);
        
        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String validationId = "VW-454";
        String message = "Critical validation failure detected.";
        String expectedGithubUrl = "https://github.com/egdcrypto/bank-of-z-modernized/issues/454";

        // Stub the activity to return a formatted Slack message body
        when(mockActivity.reportDefectToSlack(anyString(), anyString()))
            .thenAnswer(invocation -> {
                String msg = invocation.getArgument(0);
                String url = invocation.getArgument(1);
                return "Defect Reported: " + msg + " | Link: " + url;
            });

        // Act
        DefectReportingWorkflow workflow = testEnvironment.newWorkflowStub(DefectReportingWorkflow.class);
        String result = workflow.reportDefect(validationId, message, expectedGithubUrl);

        // Wait for workflow to complete
        testEnvironment.await();

        // Assert
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        
        // Verify the activity was called
        verify(mockActivity, times(1)).reportDefectToSlack(anyString(), urlCaptor.capture());
        
        // Verify the GitHub URL was passed to the activity
        String capturedUrl = urlCaptor.getValue();
        assertEquals(expectedGithubUrl, capturedUrl, "GitHub URL passed to Slack activity must match expected");

        // Verify the result (simulating the Slack body content)
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains(expectedGithubUrl), "Slack body must include GitHub URL: " + result);
    }
}
