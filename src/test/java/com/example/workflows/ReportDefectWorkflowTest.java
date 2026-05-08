package com.example.workflows;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.SlackPort;
import com.example.domain.validation.model.SlackNotificationMessage;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end regression test for S-FB-1.
 * Verifies that when a defect is reported, the resulting Slack notification
 * contains the valid GitHub issue URL.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;
    private ReportDefectActivityStub activityStub; // Manually wired for test

    @BeforeEach
    public void setUp() {
        // 1. Initialize Temporal Test Environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_REPORTING_TASK_QUEUE");

        // 2. Initialize Mocks
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();

        // 3. Register Activities and Workflows
        // We register implementations that use our mocks
        ReportDefectActivityImpl activity = new ReportDefectActivityImpl(mockSlack) {
            @Override
            public String createGitHubIssue(String description, String severity) {
                return mockGitHub.createIssue(description, severity);
            }
        };

        worker.registerActivitiesImplementations(activity);
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflowImpl.class, () -> new ReportDefectWorkflowImpl(activity));

        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_SlackBodyContainsGitHubUrl() {
        // GIVEN: Workflow client stub
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(
            ReportDefectWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue("DEFECT_REPORTING_TASK_QUEUE").build()
        );

        // WHEN: Report a defect
        String issueUrl = workflow.reportDefect("Transaction reconciliation mismatch", "LOW");

        // THEN:
        // 1. Workflow returns a URL
        assertNotNull(issueUrl);
        assertTrue(issueUrl.startsWith("https://github.com/"));

        // 2. Verify Slack Notification was triggered
        // (S-FB-1 Fix Validation: This is the core assertion)
        assertEquals(1, mockSlack.getSentMessages().size(), "Slack should have received 1 notification");

        SlackNotificationMessage message = mockSlack.getSentMessages().get(0);
        
        // Critical Check: The body must contain the URL returned by the GitHub Port
        assertTrue(
            message.text().contains(issueUrl), 
            "Slack message body must contain the GitHub issue URL. Expected to find: [" + issueUrl + "] in [" + message.text() + "]"
        );
    }

    @Test
    public void testReportDefect_SlackChannelIsCorrect() {
        // GIVEN
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(
            ReportDefectWorkflow.class,
            WorkflowOptions.newBuilder().setTaskQueue("DEFECT_REPORTING_TASK_QUEUE").build()
        );

        // WHEN
        workflow.reportDefect("Database connection failure", "HIGH");

        // THEN
        SlackNotificationMessage message = mockSlack.getSentMessages().get(0);
        assertEquals("#vforce360-issues", message.channel());
    }

    // Helper class to bridge the Activity interface with our Test-driven implementations
    private interface ReportDefectActivityStub extends ReportDefectActivity {}
}
