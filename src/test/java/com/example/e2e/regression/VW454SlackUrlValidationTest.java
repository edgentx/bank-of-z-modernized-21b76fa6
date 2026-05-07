package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.mocks.MockDefectRepository;
import com.example.mocks.MockNotificationPublisher;
import com.example.ports.DefectRepository;
import com.example.ports.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for Story S-FB-1 / Defect VW-454.
 * Validates that the Slack notification body contains the GitHub issue URL.
 */
public class VW454SlackUrlValidationTest {

    private MockNotificationPublisher slackPublisher;
    private DefectRepository defectRepository;
    private ReportDefectWorkflow workflow; // System Under Test

    @BeforeEach
    void setUp() {
        slackPublisher = new MockNotificationPublisher();
        defectRepository = new MockDefectRepository();
        
        // Inject mocks into the workflow/handler
        // Assuming a Workflow implementation exists that needs these dependencies
        // workflow = new ReportDefectWorkflowImpl(slackPublisher, defectRepository);
        
        // Since the implementation doesn't exist yet, we create a dummy SUT class 
        // just to satisfy the structure for this 'Red' phase test.
        workflow = new ReportDefectWorkflowStub();
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedGitHubUrl = "https://github.com/example/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, expectedGitHubUrl);

        // Act
        workflow.execute(cmd);

        // Assert
        // 1. Verify message was published
        assertFalse(slackPublisher.messages.isEmpty(), "No message was published to Slack");

        // 2. Verify content includes the specific GitHub URL
        MockNotificationPublisher.PublishedMessage msg = slackPublisher.messages.get(0);
        String body = msg.content();
        
        assertTrue(body.contains(expectedGitHubUrl), 
            "Slack body should contain GitHub URL: " + expectedGitHubUrl + ", but was: " + body);
    }

    // --- Stubs for Test Compilation (Red Phase) ---

    private static class ReportDefectWorkflowStub implements ReportDefectWorkflow {
        @Override
        public void execute(Command cmd) {
            // Deliberate empty implementation to make test FAIL (Red Phase)
            // In a real scenario, this would be the actual Workflow class 
            // which currently does not implement the logic.
        }
    }

    public interface ReportDefectWorkflow {
        void execute(Command cmd);
    }

    public record ReportDefectCommand(String defectId, String gitHubUrl) implements Command {}
}
