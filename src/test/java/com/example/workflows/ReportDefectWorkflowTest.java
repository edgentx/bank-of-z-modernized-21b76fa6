package com.example.workflows;

import com.example.vforce.github.model.GithubIssue;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.Workflow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * End-to-End style regression test for Defect VW-454.
 * Uses Temporal TestWorkflowEnvironment to validate the Workflow orchestration logic.
 */
class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private ReportDefectActivity mockActivity;

    @BeforeEach
    void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("VFORCE_TASK_QUEUE");
        mockActivity = Mockito.mock(ReportDefectActivity.class);

        // Register Workflow and Activity stubs
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflowImpl.class);
        worker.registerActivitiesImplementations(mockActivity);
        testEnv.start();
    }

    @AfterEach
    void tearDown() {
        testEnv.close();
    }

    @Test
    void shouldPassGitHubUrlToSlackActivity_RegressionVW454() {
        // Setup: Mock Activity behaviors
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        GithubIssue mockIssue = new GithubIssue(expectedUrl);

        when(mockActivity.createGithubIssue(any())).thenReturn(mockIssue);
        doNothing().when(mockActivity).postSlackNotification(any(), any());

        // Get Workflow stub
        ReportDefectWorkflow workflow = testEnv.newWorkflowStub(
                ReportDefectWorkflow.class,
                ReportDefectWorkflowOptions.newBuilder().setTaskQueue("VFORCE_TASK_QUEUE").build()
        );

        // Execute Workflow
        String returnedUrl = workflow.reportDefect("Critical validation error");

        // Verify: Activity 1 (GitHub) called
        verify(mockActivity).createGithubIssue(eq("Critical validation error"));

        // Verify: Activity 2 (Slack) called
        // This is the core regression check for VW-454:
        // The workflow MUST pass the result of Activity 1 into Activity 2.
        verify(mockActivity).postSlackNotification(eq("Critical validation error"), eq(mockIssue));

        // Assert result
        assertThat(returnedUrl).isEqualTo(expectedUrl);
    }

    private static class ReportDefectWorkflowOptions {
        public static Builder newBuilder() { return new Builder(); }
        public static class Builder {
            private String taskQueue;
            public Builder setTaskQueue(String tq) { this.taskQueue = tq; return this; }
            // Stub builder implementation logic is usually handled by Temporal client,
            // but for this pseudo-test class we assume the framework handles it.
            // In actual code, this is `WorkflowOptions.newBuilder()`.
        }
    }
}
