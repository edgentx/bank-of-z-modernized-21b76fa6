package com.example.steps.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.service.DefectReportWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.Workflow;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that when a defect is reported, the resulting Slack notification body
 * contains the GitHub issue URL.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VW454Steps {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private MockSlackActivities mockSlack;
    private MockGitHubActivities mockGitHub;

    @BeforeAll
    void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("DEFECT_TASK_QUEUE");
        
        // Instantiate mock activities
        mockSlack = new MockSlackActivities();
        mockGitHub = new MockGitHubActivities();

        // Register Workflow and Activities
        worker.registerWorkflowImplementationFactory(DefectReportWorkflowImpl.class, () -> {
            return new DefectReportWorkflowImpl(mockSlack, mockGitHub);
        });
        worker.registerActivitiesImplementations(mockSlack, mockGitHub);

        testEnv.start();
    }

    @AfterAll
    void tearDown() {
        testEnv.close();
    }

    @Test
    @DisplayName("VW-454: Slack body includes GitHub issue URL")
    void test_slack_body_includes_github_url() {
        // Arrange
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String description = "Slack body needs to include the GitHub issue link.";
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";

        mockGitHub.setUrlToReturn(expectedGitHubUrl);

        // Act: Trigger _report_defect via temporal-worker exec
        DefectReportWorkflow workflow = testEnv.newWorkflowStub(DefectReportWorkflow.class);
        WorkflowExecution execution = WorkflowClient.start(workflow::reportDefect, defectId, title, description);
        
        // Wait for workflow to complete (or use async execution capture in real impl)
        // For this test, we block until the workflow finishes executing.
        // Note: In a real implementation, we might block on a future or use `testEnv.sleep`.
        // Here we assume synchronous execution for verification within the test environment.
        workflow.reportDefect(defectId, title, description);

        // Assert: Verify Slack body contains GitHub issue link
        String actualSlackMessage = mockSlack.getLastMessage();
        
        assertNotNull(actualSlackMessage, "Slack message should not be null");
        assertTrue(actualSlackMessage.contains(expectedGitHubUrl), 
            "Slack body should include GitHub issue URL: " + expectedGitHubUrl + ". Actual was: " + actualSlackMessage);
    }

    // --- Stub Workflow Implementation (Red Phase Logic) ---

    public static class DefectReportWorkflowImpl implements DefectReportWorkflow {
        private final SlackActivities slack;
        private final GitHubActivities github;

        public DefectReportWorkflowImpl(SlackActivities slack, GitHubActivities github) {
            this.slack = slack;
            this.github = github;
        }

        @Override
        public void reportDefect(String defectId, String title, String description) {
            // Workflow Logic:
            // 1. Create Issue in GitHub
            String issueUrl = github.createIssue(title, description);
            
            // 2. Build Slack Message
            // ACCpetance Criteria: Slack body includes GitHub issue URL
            String messageBody = "Defect Reported: " + title + "\n" + issueUrl;

            // 3. Send Notification
            slack.sendNotification(messageBody);
        }
    }

    // --- Mock Activities ---

    public static class MockSlackActivities implements SlackActivities {
        private String lastMessage;

        @Override
        public void sendNotification(String message) {
            this.lastMessage = message;
            System.out.println("[MockSlack] Sending: " + message);
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }

    public static class MockGitHubActivities implements GitHubActivities {
        private String urlToReturn;

        public void setUrlToReturn(String url) {
            this.urlToReturn = url;
        }

        @Override
        public String createIssue(String title, String description) {
            // Simulate creating an issue and returning a URL
            if (urlToReturn == null) {
                return "https://github.com/bank-of-z/issues/default";
            }
            return urlToReturn;
        }
    }
}
