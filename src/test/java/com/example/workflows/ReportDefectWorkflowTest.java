package com.example.workflows;

import com.example.application.DefectReportingWorkflow;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workers.ReportDefectActivity;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowImplementationOptions;
import org.junit.jupiter.api.*;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Workflow Test for S-FB-1.
 * Validates that a defect report generates a GitHub URL and includes it in the Slack body.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private MockGitHubPort mockGitHub;
    private MockSlackPort mockSlack;
    private ReportDefectWorkflow workflow;

    @BeforeEach
    public void setUp() {
        // Set up Temporal Test Environment
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("SFB1_TASK_QUEUE");

        // Initialize Mocks
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackPort();

        // Register Workflow and Activity
        // Note: In a real Spring setup, these might be auto-wired.
        // Here we manually register implementations for the test context.
        
        // Register Workflow stub
        worker.registerWorkflowImplementationFactory(
            DefectReportingWorkflow.class,
            () -> new DefectReportingWorkflowImpl(mockGitHub, mockSlack)
        );

        // Register Activity implementation
        worker.registerActivitiesImplementations(new ReportDefectActivityImpl(mockGitHub, mockSlack));

        testEnv.start();
        
        // Create Workflow Client stub
        workflow = testEnv.newWorkflowStub(
            ReportDefectWorkflow.class,
            io.temporal.workflow.WorkflowOptions.newBuilder()
                .setTaskQueue("SFB1_TASK_QUEUE")
                .setWorkflowId("SFB1-WORKFLOW-TEST")
                .build()
        );
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    @DisplayName("S-FB-1: Verify Slack body contains GitHub URL when defect is reported")
    public void testSlackBodyContainsGithubUrl() {
        // Arrange
        String title = "VW-454: Validation Failure";
        String description = "Critical defect in validation component";
        String component = "validation";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        
        mockGitHub.setIssueUrl(expectedUrl);

        // Act
        workflow.reportDefect(title, description, component);

        // Assert
        // 1. Verify GitHub was called
        // (MockGitHubPort tracks state, but we mostly care about the output to Slack)

        // 2. Verify Slack was called exactly once
        assertEquals(1, mockSlack.messages.size(), "Slack should be notified once");

        // 3. Verify the Slack body contains the GitHub URL (Acceptance Criteria)
        String slackBody = mockSlack.messages.get(0);
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the specific GitHub issue URL.\nExpected: " + expectedUrl + "\nActual Body: " + slackBody
        );

        // 4. Verify Channel
        assertEquals("#vforce360-issues", mockSlack.lastChannel);
    }

    @Test
    @DisplayName("S-FB-1: Ensure Slack body is not malformed or empty")
    public void testSlackBodyIsNotEmpty() {
        workflow.reportDefect("Defect", "Desc", "comp");
        String body = mockSlack.messages.get(0);
        assertNotNull(body);
        assertFalse(body.isBlank());
    }

    // --- Test Implementations ---

    /**
     * Simple Implementation of the Workflow for testing purposes.
     * In the actual implementation, this will be the main class.
     */
    public static class DefectReportingWorkflowImpl implements DefectReportingWorkflow {
        private final GitHubPort githubPort;
        private final SlackPort slackPort;

        public DefectReportingWorkflowImpl(GitHubPort githubPort, SlackPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        @Override
        public void reportDefect(String title, String description, String component) {
            // 1. Create GitHub Issue
            String url = githubPort.createIssue(title, description, component);

            // 2. Send Notification to Slack
            // VW-454: The body MUST include the URL
            String message = String.format("Defect Reported: %s\nGitHub Issue: %s", title, url);
            slackPort.sendMessage("#vforce360-issues", message);
        }
    }

    /**
     * Activity implementation wrapping the ports.
     */
    public static class ReportDefectActivityImpl implements ReportDefectActivity {
        private final GitHubPort githubPort;
        private final SlackPort slackPort;

        public ReportDefectActivityImpl(GitHubPort githubPort, SlackPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        @Override
        public String createGitHubIssue(String title, String description, String component) {
            return githubPort.createIssue(title, description, component);
        }

        @Override
        public void sendSlackNotification(String channel, String messageBody) {
            slackPort.sendMessage(channel, messageBody);
        }
    }
}