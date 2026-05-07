package com.example.steps;

import com.example.domain.validation.ReportDefectCmd;
import com.example.mocks.*;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RED PHASE: Failing tests for S-FB-1.
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (Missing GitHub URL).
 * 2. Regression test added to e2e/regression/ covering this scenario.
 * 
 * Component: Validation (Temporal Workflow)
 * Context: _report_defect workflow execution.
 */
public class ReportDefectSteps {

    // Mocks for external dependencies (Adapter Pattern)
    private MockGitHubPort mockGitHub;
    private MockSlackPort mockSlack;
    private MockValidationRepository mockRepo;

    // Temporal Test Environment
    private TestWorkflowEnvironment testEnv;
    private Worker worker;

    @BeforeEach
    public void setUp() {
        // Initialize Mocks
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackPort();
        mockRepo = new MockValidationRepository();

        // Initialize Temporal Test Environment
        // This requires io.temporal:temporal-testing in pom.xml
        testEnv = TestWorkflowEnvironment.newInstance();
        
        // Register the Workflow (Implementation does not exist yet, this will fail red)
        // worker = testEnv.newWorker("VALIDATION_TASK_QUEUE");
        // worker.registerWorkflowImplementationFactory(ReportDefectWorkflow.class, 
        //     () -> new ReportDefectWorkflowImpl(mockGitHub, mockSlack, mockRepo));
        // testEnv.start();
    }

    @AfterEach
    public void tearDown() {
        if (testEnv != null) {
            testEnv.close();
        }
    }

    /**
     * AC Regression: Verify Slack body contains GitHub issue link.
     * 
     * Given a defect command is triggered via temporal-worker exec
     * When the workflow processes the command
     * Then the Slack body includes the GitHub issue URL
     */
    @Test
    @Tag("e2e")
    @Tag("regression")
    @DisplayName("S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)")
    public void testReportDefect_generatesSlackMessageWithGitHubLink() {
        // 1. Trigger _report_defect via temporal-worker exec
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String severity = "LOW";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "Description of defect...",
            projectId,
            severity
        );

        // Configure Mocks
        // Simulate GitHub returning a URL upon creation
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHub.setMockCreatedIssueUrl(defectId, expectedGitHubUrl);

        // 2. Verify Slack body contains GitHub issue link
        // Note: Since the workflow implementation doesn't exist yet, 
        // we will manually trigger the logic path we expect in the real impl
        // to make the test compile and run (and fail because dependencies are null).
        // In a real RED phase, we might skip execution if the Workflow class isn't compiled,
        // but here we assert on the state of the Mocks after the "execution".

        try {
            // This class does not exist yet -> Compilation failure (Red)
            // ReportDefectWorkflow workflow = mockRepo.newWorkflowStub(ReportDefectWorkflow.class);
            // workflow.report(cmd);
            
            // Simulate the behavior for the sake of the Red Phase structure:
            // The actual implementation will be injected by Temporal.
            // For now, we assume the logic calls the ports.
            
            // Hypothetical call sequence (for test logic construction):
            // String url = mockGitHub.createIssue(cmd);
            // mockSlack.notify(cmd, url);
            
            throw new UnsupportedOperationException("Implementation missing: ReportDefectWorkflow not found.");
            
        } catch (Exception e) {
            // Expected to fail in Red Phase
        }

        // Assertions on the Mocks to prove behavior
        // We expect the GitHub port to have been called
        assertTrue(mockGitHub.wasCreateIssueCalled(), "GitHub: createIssue should have been called");
        assertEquals(expectedGitHubUrl, mockGitHub.getLastGeneratedUrl(), "GitHub: Should return specific URL");

        // We expect the Slack port to have been called with the URL included
        assertTrue(mockSlack.wasNotificationSent(), "Slack: notification should have been sent");
        
        // CRITICAL ASSERTION FOR S-FB-1
        Map<String, Object> lastMessageContext = mockSlack.getLastMessageContext();
        assertNotNull(lastMessageContext, "Slack: Message context should not be null");
        
        String actualSlackBody = (String) lastMessageContext.get("body");
        assertNotNull(actualSlackBody, "Slack: Message body should not be null");
        
        // Check for the specific defect ID link format or the full URL
        // The Defect states: "Verify Slack body contains GitHub issue link"
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body MUST include the GitHub issue URL (" + expectedGitHubUrl + "). " +
            "Actual body: " + actualSlackBody
        );
    }
}
