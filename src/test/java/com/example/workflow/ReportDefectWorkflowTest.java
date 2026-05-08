package com.example.workflow;

import com.example.mocks.MockDefectReportingActivitiesImpl;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * TDD Red Phase Test for S-FB-1.
 * This test expects the Slack notification body to contain the GitHub issue URL.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockDefectReportingActivitiesImpl activitiesImpl;

    @BeforeEach
    public void setUp() {
        // Initialize the Temporal test environment
        // Note: If Temporal dependencies are missing, this setup will fail to compile,
        // highlighting the dependency requirements.
        try {
            testEnvironment = TestWorkflowEnvironment.newInstance();
            activitiesImpl = new MockDefectReportingActivitiesImpl();
            worker = testEnvironment.newWorker("TASK_QUEUE");
            worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
            worker.registerActivitiesImplementations(activitiesImpl);
            testEnvironment.start();
        } catch (NoClassDefFoundError e) {
            // Dependencies missing, fail fast
            fail("Temporal SDK dependencies missing. Ensure pom.xml includes io.temporal:temporal-testing");
        }
    }

    @AfterEach
    public void tearDown() {
        if (testEnvironment != null) {
            testEnvironment.close();
        }
    }

    @Test
    public void testReportDefect_SlackBodyContainsGithubUrl() {
        // Arrange
        String defectId = "S-FB-1";
        String description = "GitHub URL in Slack body (end-to-end)";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        
        // Configure the mock activity to return a specific GitHub URL
        activitiesImpl.setGithubUrlToReturn(expectedUrl);

        // Get workflow stub
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);

        // Act
        // Execute the workflow. In a real scenario, this triggers the temporal worker.
        workflow.reportDefect(defectId, description);

        // Wait for workflow completion (synchronous for test)
        // Assert
        String actualSlackBody = activitiesImpl.getLastSlackMessage();
        
        // TDD Red Phase Expectation:
        // We expect the URL to be present in the message.
        // If the implementation is missing or broken, this assertion fails.
        assertTrue(actualSlackBody != null, "Slack body should not be null");
        assertTrue(actualSlackBody.contains(expectedUrl), 
            "Slack body must contain the GitHub URL. Expected: " + expectedUrl + " in body: " + actualSlackBody);
    }
}