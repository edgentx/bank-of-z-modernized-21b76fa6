package com.example.domain.reconciliation;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Verifies that when _report_defect is triggered, the Slack body contains
 * the GitHub issue link.
 */
class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackNotificationPort mockSlackPort;

    @BeforeEach
    void setUp() {
        // Initialize the Temporal test environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        
        // Initialize the mock adapter
        mockSlackPort = new MockSlackNotificationPort();
        
        // Register the Workflow and Activity implementations
        // Note: We assume a class 'ReportDefectWorkflowImpl' exists to be tested.
        // If this class doesn't exist, the build will fail (Red Phase).
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");
        
        // Register the Workflow stub
        // In a real setup, we'd register the actual Workflow class here.
        // For the purpose of this TDD Red Phase, we are defining the contract.
        // worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        
        testEnvironment.start();
    }

    @AfterEach
    void tearDown() {
        testEnvironment.close();
    }

    @Test
    void testReportDefect_includesGitHubUrlInSlackBody() {
        // Given: A defect report trigger with a specific GitHub URL
        String expectedGitHubUrl = "https://github.com/example-org/project/issues/454";
        
        // When: The workflow is executed
        // ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        // workflow.reportDefect(expectedGitHubUrl);
        
        // Then: Verify the Slack payload contains the GitHub URL
        // String sentPayload = mockSlackPort.getLastPayload();
        // assertTrue(sentPayload.contains(expectedGitHubUrl), 
        //     "Slack body should include the GitHub issue URL: " + expectedGitHubUrl);
        
        // RED PHASE ASSERTION (Failing until implementation exists)
        fail("VW-454: Implementation missing. Slack payload verification could not be performed for URL: " + expectedGitHubUrl);
    }
}