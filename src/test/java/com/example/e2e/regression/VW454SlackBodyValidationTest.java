package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;
import com.example.workflows.ReportDefectWorkflow;
import com.example.workflows.ReportDefectWorkflowImpl;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for S-FB-1 / VW-454.
 * Verifies that when a defect is reported, the resulting Slack notification body
 * contains the link to the created GitHub issue.
 */
public class VW454SlackBodyValidationTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackNotifier mockSlackNotifier;
    private MockGitHubClient mockGitHubClient;
    private ReportDefectWorkflow workflow;

    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/legacy-issues/issues/454";

    @BeforeEach
    public void setUp() {
        // Initialize Mocks
        mockSlackNotifier = new MockSlackNotifier();
        mockGitHubClient = new MockGitHubClient();
        mockGitHubClient.setNextIssueUrl(EXPECTED_GITHUB_URL);

        // Initialize Temporal Test Environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("DEFECT_TASK_QUEUE");

        // Register Workflow and Activities with Mocks injected
        // Note: In a real Spring setup, we would use a mock bean definition.
        // Here we manually wire the implementation for the isolation test.
        ReportDefectWorkflowImpl workflowImpl = new ReportDefectWorkflowImpl();
        workflowImpl.setSlackNotifier(mockSlackNotifier);
        workflowImpl.setGithubClient(mockGitHubClient);

        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        testEnvironment.start();

        // Create client stub
        workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String title = "Fix: Validating VW-454";
        String description = "Slack body validation failed.";

        // Act
        // Triggering the temporal workflow
        String result = workflow.reportDefect(projectId, title, description);

        // Assert
        assertNotNull(result, "Workflow should return a defect ID");

        // Verify external interaction: GitHub was called
        assertTrue(mockGitHubClient.createIssueCalled, "GitHub client should have been invoked to create an issue");

        // Verify external interaction: Slack was called
        assertTrue(mockSlackNotifier.notificationSent, "Slack notifier should have been invoked");

        // Critical Validation for VW-454:
        // The Slack body must contain the GitHub issue URL.
        String slackBody = mockSlackNotifier.lastText;
        assertNotNull(slackBody, "Slack body should not be null");
        
        assertTrue(
            slackBody.contains(EXPECTED_GITHUB_URL),
            "Slack body text must contain the GitHub issue URL. Expected to contain: " + EXPECTED_GITHUB_URL + "\nActual Body: " + slackBody
        );
    }

    @Test
    public void testReportDefect_ShouldFailIfSlackBodyMissingUrl() {
        // This test validates the negative case or configuration failure
        // If the mock GitHub returns a URL, but the adapter fails to put it in Slack, this catches it.

        // Arrange
        String projectId = "21b76fa6";
        String title = "Validation Test";
        String description = "Testing...";

        // Act
        workflow.reportDefect(projectId, title, description);

        // Assert
        String slackBody = mockSlackNotifier.lastText;
        
        // If the URL is missing, this assertion fails, satisfying the 'Red' phase requirement for a missing feature/bug.
        if (slackBody != null) {
            assertTrue(slackBody.contains("http"), "Body must have a link");
        } else {
            fail("Slack body was null, cannot validate URL presence.");
        }
    }
}