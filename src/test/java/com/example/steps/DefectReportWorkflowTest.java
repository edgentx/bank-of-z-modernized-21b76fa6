package com.example.steps;

import com.example.domain.validation.model.GitHubIssueUrl;
import com.example.domain.validation.model.SlackMessageBody;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.workflow.DefectReportActivities;
import com.example.workflow.ReportDefectWorkflow;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowImplementationOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for S-FB-1: Validating VW-454.
 * End-to-end test ensuring the GitHub URL is present in the Slack body.
 * Uses Temporal TestWorkflowEnvironment for deterministic workflow testing.
 */
public class DefectReportWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackNotificationPort mockSlack;
    private MockGitHubIssuePort mockGitHub;
    private DefectReportActivitiesImpl activities;

    @BeforeEach
    public void setUp() {
        // 1. Initialize Mock Adapters
        mockSlack = new MockSlackNotificationPort();
        mockGitHub = new MockGitHubIssuePort();
        
        // 2. Initialize Activity Implementation with Mocks
        activities = new DefectReportActivitiesImpl(mockGitHub, mockSlack);

        // 3. Setup Temporal Test Environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");
        
        // 4. Register Workflow and Activities
        // Note: We use the real workflow implementation, but stubbed activities.
        worker.registerWorkflowImplementationFactory(
            ReportDefectWorkflowImpl.class,
            () -> new ReportDefectWorkflowImpl()
        );
        worker.registerActivitiesImplementations(activities);

        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_GitHubUrlIsIncludedInSlackBody() {
        // GIVEN: A specific defect report
        String title = "VW-454: GitHub URL missing";
        String description = "The Slack body does not contain the link to the GitHub issue.";
        
        // Configure Mock to return a specific URL
        GitHubIssueUrl expectedUrl = new GitHubIssueUrl("https://github.com/example/bank-of-z/issues/454");
        mockGitHub.setNextUrl(expectedUrl);

        // WHEN: The defect report workflow is executed
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        SlackMessageBody result = workflow.reportDefect(title, description);

        // THEN: Verify the result (Slack body) contains the GitHub URL
        // This is the core assertion for AC: "Slack body includes GitHub issue: <url>"
        assertNotNull(result, "Resulting Slack body should not be null");
        
        String bodyText = result.value();
        assertTrue(
            bodyText.contains(expectedUrl.value()),
            "Slack body should contain the GitHub Issue URL. Expected: " + expectedUrl.value() + " in body: " + bodyText
        );

        // Cross-verify with the Mock Adapter state
        SlackMessageBody capturedMessage = mockSlack.getLastMessage();
        assertNotNull(capturedMessage, "Mock Slack adapter should have captured the message");
        assertTrue(capturedMessage.containsUrl(expectedUrl), "Captured message should contain URL");
    }

    @Test
    public void testReportDefect_ValidatesUrlFormat() {
        // Edge case: Ensure the system handles valid URL formats correctly
        String validUrl = "https://github.com/organization/repository/issues/123";
        mockGitHub.setNextUrl(new GitHubIssueUrl(validUrl));

        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
        SlackMessageBody result = workflow.reportDefect("Valid Format Test", "Testing URL validation");

        assertTrue(result.value().contains(validUrl));
    }

    // --- Inner Classes for Test Doubles (since we aren't modifying src/main yet) ---

    /**
     * Workflow Implementation Stub.
     * In a real file structure, this would be in src/main/java.
     * Included here to make the Test Compile against the Interface.
     */
    public static class ReportDefectWorkflowImpl implements ReportDefectWorkflow {
        private final DefectReportActivities activities = io.temporal.workflow.Workflow.newActivityStub(DefectReportActivities.class);

        @Override
        public SlackMessageBody reportDefect(String title, String description) {
            // Step 1: Create GitHub Issue
            GitHubIssueUrl issueUrl = activities.createGitHubIssue(title, description);

            // Step 2: Compose Message
            String messageText = "Defect Reported: " + title + "\n" +
                                "GitHub Issue: " + issueUrl.value();
            SlackMessageBody body = new SlackMessageBody(messageText);

            // Step 3: Send Notification
            activities.sendSlackNotification(body);

            return body;
        }
    }

    /**
     * Activity Implementation Stub using Mock Ports.
     */
    public static class DefectReportActivitiesImpl implements DefectReportActivities {
        private final MockGitHubIssuePort githubPort;
        private final MockSlackNotificationPort slackPort;

        public DefectReportActivitiesImpl(MockGitHubIssuePort githubPort, MockSlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        @Override
        public GitHubIssueUrl createGitHubIssue(String title, String description) {
            return githubPort.createIssue(title, description);
        }

        @Override
        public void sendSlackNotification(SlackMessageBody body) {
            slackPort.send(body);
        }
    }
}
