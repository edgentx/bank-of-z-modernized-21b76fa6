package com.example.workflow;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression test for VW-454.
 * Verifies that when reporting a defect via Temporal,
 * the resulting Slack message body contains the GitHub issue URL.
 */
public class ReportDefectWorkflowTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackPort mockSlack;
    private GitHubPort gitHub;

    @BeforeEach
    public void setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("VFORCE360_TASK_QUEUE");
        
        // Instantiate Mocks
        mockSlack = new MockSlackPort();
        gitHub = new MockGitHubPort();

        // Register Workflow and Activities with Mock dependencies
        // Note: In a real Spring setup, these would be wired via context.
        // For unit testing Temporal logic outside of Spring, we register stubs/workers directly.
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflowImpl.class, () -> new ReportDefectWorkflowImpl(mockSlack, gitHub));
        
        testEnvironment.start();
    }

    @AfterEach
    public void tearDown() {
        testEnvironment.close();
    }

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String issueId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/issues/issues/" + issueId;
        
        ReportDefectWorkflow workflow = testEnvironment.newWorkflowStub(ReportDefectWorkflow.class);

        // Act
        workflow.reportDefect(issueId, "Defect reported by user.");

        // Assert
        // Validation: The mock Slack adapter should have captured a message containing the expected URL
        boolean found = mockSlack.receivedMessageContaining(expectedUrl);
        
        assertTrue(found, "Expected Slack body to include GitHub issue URL: " + expectedUrl + ", but it was not found in messages: " + mockSlack.messages);
    }
}
