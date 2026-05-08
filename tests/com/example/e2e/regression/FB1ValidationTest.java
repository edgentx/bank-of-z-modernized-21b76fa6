package com.example.e2e.regression;

import com.example.application.DefectWorkflowService;
import com.example.domain.defect.model.DefectAggregate;
import com.example.infrastructure.workflow.DefectReportingWorkflow;
import com.example.infrastructure.workflow.DefectReportingWorkflowImpl;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * F-B-1: Regression test validating VW-454.
 * Expected: Slack body contains GitHub URL.
 */
@SpringBootTest
public class FB1ValidationTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;
    private DefectReportingWorkflow workflowStub;

    @BeforeEach
    public void setUp() {
        // 1. Setup Temporal Test Environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");
        
        // 2. Initialize Mocks
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();
        
        // 3. Register Workflow Implementation
        // In a real Spring app, these would be autowired. 
        // Here we manually wire the workflow implementation with our mocks.
        DefectReportingWorkflowImpl workflowImpl = new DefectReportingWorkflowImpl(mockSlack, mockGitHub);
        worker.registerWorkflowImplementationTypes(DefectReportingWorkflowImpl.class);
        
        testEnvironment.start();
        
        // 4. Create Workflow Stub
        workflowStub = testEnvironment.newWorkflowStub(DefectReportingWorkflow.class);
    }

    @Test
    public void testReportDefect_SlackBodyContainsGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454 Regression";
        String defectDesc = "Slack body missing URL";
        String expectedUrl = "https://github.com/mock-bank/test-issues/454";
        mockGitHub.setNextIssueUrl(expectedUrl);

        // Act
        // Trigger _report_defect via temporal-worker exec
        String actualUrl = workflowStub.reportDefect(defectTitle, defectDesc);

        // Assert
        // 1. Verify workflow returned URL
        assertEquals(expectedUrl, actualUrl, "Workflow should return the GitHub issue URL");
        
        // 2. Verify Slack body contains URL (VW-454 Validation)
        assertTrue(mockSlack.wasUrlSent(expectedUrl), 
            "Slack message body MUST contain the GitHub issue URL (VW-454). " +
            "Expected URL: " + expectedUrl + " " +
            "Actual Body: " + mockSlack.lastBody);
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }
}
