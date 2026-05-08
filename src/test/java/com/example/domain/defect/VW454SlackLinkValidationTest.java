package com.example.domain.defect;

import com.example.ports.SlackNotifier;
import com.example.mocks.FakeSlackNotifier;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End regression test for defect VW-454.
 * Verifies that the Slack body generated during defect reporting
 * contains the GitHub issue URL.
 *
 * Context: "Trigger _report_defect via temporal-worker exec, Verify Slack body contains GitHub issue link"
 */
public class VW454SlackLinkValidationTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private FakeSlackNotifier fakeSlack;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("VFORCE360_TASK_QUEUE");
        
        // Instantiate our test double (Mock Adapter)
        fakeSlack = new FakeSlackNotifier();
        
        // Register the workflow and activities. 
        // Note: DefectReportingWorkflow and ReportDefectActivity are assumed implementations 
        // that would be created to satisfy the defect fix.
        // We register the stubs/mocks here to satisfy the Temporal test environment wiring.
        
        // Workflow Stub (Unit test style injection would happen in the factory, 
        // but for TestWorkflowEnvironment we register the implementations).
        // For RED phase, we assume these classes don't exist or are stubs.
        
        // worker.registerWorkflowImplementationFactory(DefectReportingWorkflowImpl.class, () -> ...);
        // worker.registerActivitiesImplementations(new ReportDefectActivityImpl(fakeSlack));
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    public void testReportDefect_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454 Regression Check";
        String defectDescription = "Verifying URL presence in Slack payload.";
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";

        // Act
        // Here we would invoke the workflow using testEnv:
        // 
        // DefectReportingWorkflow workflow = testEnv.newWorkflowStub(DefectReportingWorkflow.class);
        // WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue("VFORCE360_TASK_QUEUE").build();
        // workflow.reportDefect(defectTitle, defectDescription);
        // 
        // Since this is the RED phase and we are setting up the structure, 
        // we are asserting the contract expectations.
        
        // Assert (Verification Phase)
        // The implementation should have called sendNotification on our mock.
        List<String> capturedPayloads = fakeSlack.getCapturedPayloads();
        
        // RED Phase: We expect 1 notification sent for the defect report.
        // If implementation is missing, this list is empty.
        assertFalse(capturedPayloads.isEmpty(), "No Slack notification was triggered by the workflow");

        String actualPayload = capturedPayloads.get(0);
        
        // AC: Verify Slack body contains GitHub issue: <url>
        assertTrue(
            actualPayload.contains(expectedGitHubUrl), 
            "Slack body must contain the GitHub issue URL. Payload was: " + actualPayload
        );
        
        // Additional sanity checks for structure
        assertTrue(actualPayload.contains("\"text\"") || actualPayload.contains("blocks"), 
            "Payload does not look like a valid Slack message format");
    }
}
