package com.example.e2e.regression;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression Test for Story S-FB-1.
 * Validates VW-454: Ensure GitHub URL is present in the Slack body when a defect is reported.
 * 
 * Testing strategy: Use Temporal TestWorkflowEnvironment to execute the workflow
  * logic with a mocked Slack port.
 */
@SpringBootTest
class VW454ValidationTest {

    private TestWorkflowEnvironment testEnvironment;
    private Worker worker;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        // Initialize Temporal test environment
        testEnvironment = TestWorkflowEnvironment.newInstance();
        worker = testEnvironment.newWorker("VFORCE360_TASK_QUEUE");
        
        // Initialize Mock
        mockSlack = new MockSlackNotificationPort();
        
        // Register workflow and activities with mocks
        // Note: In a real Spring setup, these might be auto-wired, but here we register explicitly for isolation.
        worker.registerWorkflowImplementationTypes(DefectReportingWorkflowImpl.class);
        
        worker.registerActivitiesImplementations(new DefectReportingActivity(mockSlack));
        
        testEnvironment.start();
    }

    @AfterEach
    void tearDown() {
        testEnvironment.close();
    }

    @Test
    void testGitHubUrlInSlackBody() {
        // ARRANGE
        String expectedGithubUrl = "https://github.com/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "GitHub URL missing in Slack",
            "We need to verify the link is present",
            Map.of("severity", "LOW")
        );

        // ACT
        // Execute the workflow via Temporal test framework
        DefectReportingWorkflow workflow = testEnvironment.newWorkflowStub(DefectReportingWorkflow.class);
        DefectReportedEvent result = workflow.reportDefect(cmd);

        // ASSERT
        // 1. Verify the workflow returned the event with the URL
        assertThat(result).isNotNull();
        assertThat(result.githubIssueUrl()).isEqualTo(expectedGithubUrl);

        // 2. Verify the external dependency (Slack) was called correctly
        // This is the core validation for VW-454.
        assertThat(mockSlack.lastMessageContainsUrl(expectedGithubUrl))
            .withFailMessage("Slack body should contain GitHub issue URL: " + expectedGithubUrl)
            .isTrue();
        
        assertThat(mockSlack.getMessages()).hasSize(1);
        assertThat(mockSlack.getMessages().get(0).channelId).isEqualTo("#vforce360-issues");
    }

    // --- Workflow Interface ---
    @WorkflowInterface
    public interface DefectReportingWorkflow {
        @WorkflowMethod
        DefectReportedEvent reportDefect(ReportDefectCmd cmd);
    }

    // --- Workflow Implementation (Stub for TDD Red Phase) ---
    public static class DefectReportingWorkflowImpl implements DefectReportingWorkflow {
        private final DefectReportingActivity activity = new DefectReportingActivity(new MockSlackNotificationPort());

        @Override
        public DefectReportedEvent reportDefect(ReportDefectCmd cmd) {
            // Real implementation would go here
            return new DefectReportedEvent(
                cmd.defectId(), 
                "https://github.com/bank-of-z/issues/454", 
                cmd.metadata(), 
                Instant.now()
            );
        }
    }

    // --- Activity Interface ---
    public interface DefectReportingActivity {
        void notifySlack(String channelId, String message);
    }

    // --- Activity Implementation (Stub for TDD Red Phase) ---
    public static class DefectReportingActivity implements DefectReportingActivity {
        private final SlackNotificationPort slackPort;

        public DefectReportingActivity(SlackNotificationPort slackPort) {
            this.slackPort = slackPort;
        }

        @Override
        public void notifySlack(String channelId, String message) {
            // Intentionally doing nothing or doing wrong to satisfy Red Phase requirements initially,
            // but the Mock captures the call.
            slackPort.postMessage(channelId, message);
        }
    }
}