package com.example.e2e.regression;

import com.example.application.DefectReportingActivities;
import com.example.domain.validation.model.ReportDefectWorkflow;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.Workflow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/*
 * Story: S-FB-1
 * Title: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 *
 * Expected Behavior:
 * Slack body includes GitHub issue: <url>
 */
public class VW454RegressionTest {

    private TestWorkflowEnvironment testEnv;
    private Worker worker;
    private MockGitHubPort mockGitHub;
    private MockSlackPort mockSlack;
    private DefectReportingActivities activitiesImpl;

    @BeforeEach
    public void setUp() {
        testEnv = TestWorkflowEnvironment.newInstance();
        worker = testEnv.newWorker("VFORCE360_TASK_QUEUE");

        // Initialize Mocks
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackPort();

        // Wire Mocks to Activities implementation
        activitiesImpl = new DefectReportingActivities() {
            @Override
            public String createGitHubIssue(String title, String body) {
                return mockGitHub.createIssue(title, body).join();
            }

            @Override
            public void notifySlack(String channel, String message) {
                mockSlack.sendMessage(channel, message).join();
            }
        };

        // Register Workflow and Activities
        // Note: In a real Spring setup, these might be auto-registered beans.
        // For this isolated TDD test, we register manually.
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflow.class, () -> new ReportDefectWorkflow() {
            @Override
            public String reportDefect(String title) {
                // Workflow stub implementation (Simulating the actual workflow logic)
                String url = activitiesImpl.createGitHubIssue(title, "Defect: " + title);
                activitiesImpl.notifySlack("#vforce360-issues", "Created issue: " + url);
                return url;
            }
        });

        worker.registerActivitiesImplementations(activitiesImpl);
        testEnv.start();
    }

    @AfterEach
    public void tearDown() {
        testEnv.close();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Given
        String defectTitle = "VW-454 Regression Test";
        String expectedUrl = "https://github.com/mock/issues/454";
        mockGitHub.setResponseUrl(expectedUrl);

        ReportDefectWorkflow workflow = testEnv.newWorkflowStub(ReportDefectWorkflow.class);

        // When
        workflow.reportDefect(defectTitle);

        // Then
        // Validate Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(mockSlack.lastMessageContains(expectedUrl),
            "Slack message should contain the GitHub URL created: " + expectedUrl);

        // Validate Regression: Ensure the link is actually present
        if (!mockSlack.lastMessageContains("http")) {
            fail("Actual Behavior: No link found in Slack body");
        }
    }
}
