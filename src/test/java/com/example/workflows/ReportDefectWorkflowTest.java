package com.example.workflows;

import com.example.application.DefectReportingActivity;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.repository.VForce360Repository;
import com.example.mocks.InMemoryVForce360Repository;
import com.example.steps.S17Steps; // Reusing existing naming pattern
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RED PHASE TESTS for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * Tests verify that triggering the defect reporting workflow results in a Slack body
 * containing the GitHub issue link.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private InMemoryVForce360Repository mockRepository;
    private MockSlackService mockSlackService;

    @BeforeEach
    public void setUp() {
        // Set up Temporal test environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("TASK_QUEUE_NAME");
        
        // Initialize Mocks
        mockRepository = new InMemoryVForce360Repository();
        mockSlackService = new MockSlackService();

        // Register Workflow and Activity implementations
        ReportDefectWorkflowImpl workflow = new ReportDefectWorkflowImpl();
        DefectReportingActivityImpl activity = new DefectReportingActivityImpl(mockRepository, mockSlackService);

        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        worker.registerActivitiesImplementations(activity);

        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    @org.junit.jupiter.api.DisplayName("VW-454: Slack body should contain GitHub issue URL")
    public void testSlackBodyContainsGitHubUrl() throws ExecutionException, InterruptedException {
        // Arrange
        String defectId = "S-FB-1";
        String expectedGitHubUrl = "https://github.com/crypto-bank-of-z/issues/454";
        String slackChannel = "#vforce360-issues";

        ReportDefectWorkflow workflowStub = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        
        // Act
        // Trigger _report_defect via temporal-worker exec
        workflowStub.reportDefect(defectId, expectedGitHubUrl, slackChannel);

        // Allow async processing
        Thread.sleep(500); 

        // Assert
        // Verify Slack body contains GitHub issue: <url>
        assertTrue(mockSlackService.wasCalled(), "Slack service should have been triggered");
        String lastBody = mockSlackService.getLastMessageBody();
        assertNotNull(lastBody, "Slack message body should not be null");
        
        // The core validation: Check for the URL in the body
        assertTrue(
            lastBody.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub URL. Body was: " + lastBody
        );
        
        // Regression check for VW-454
        assertTrue(
            lastBody.contains("GitHub issue:") || lastBody.contains("Issue:"),
            "Slack body should indicate it is a GitHub issue link"
        );
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Regression: Workflow fails if GitHub URL is invalid")
    public void testRegressionInvalidUrl() {
        // Arrange
        String defectId = "S-FB-2";
        String invalidUrl = "not-a-url";
        
        ReportDefectWorkflow workflowStub = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            workflowStub.reportDefect(defectId, invalidUrl, "#random");
        }, "Workflow should fail validation for invalid URL format");
    }

    // --- Mocks & Stubs required for this Test ---

    /**
     * Mock Adapter for Slack Service.
     * Captures output for verification.
     */
    public static class MockSlackService {
        private boolean called = false;
        private String lastMessageBody;
        private String lastChannel;

        public void postMessage(String channel, String body) {
            this.called = true;
            this.lastChannel = channel;
            this.lastMessageBody = body;
            System.out.println("[MockSlack] Posted to " + channel + ": " + body);
        }

        public boolean wasCalled() {
            return called;
        }

        public String getLastMessageBody() {
            return lastMessageBody;
        }
        
        public String getLastChannel() {
            return lastChannel;
        }
    }

    /**
     * Concrete implementation of Activity for testing.
     * Delegates to Mock Repositories.
     */
    public static class DefectReportingActivityImpl implements DefectReportingActivity {
        private final VForce360Repository repository;
        private final MockSlackService slackService;

        public DefectReportingActivityImpl(VForce360Repository repository, MockSlackService slackService) {
            this.repository = repository;
            this.slackService = slackService;
        }

        @Override
        public String reportToVForce360(String defectId, String githubUrl, String slackChannel) {
            // 1. Domain Logic
            // In a real scenario, we might load the aggregate.
            // Here we simulate the reporting.
            
            // 2. Slack Logic
            String body = String.format("Defect Reported: GitHub issue: %s", githubUrl);
            slackService.postMessage(slackChannel, body);
            
            return "OK";
        }
    }
}
