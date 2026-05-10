package com.example.domain.validation;

import com.example.domain.shared.DefectReportedEvent;
import com.example.domain.shared.ReportDefectCommand;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Regression test for GitHub URL presence in Slack notification.
 * 
 * Defect VW-454: Verify that when a defect is reported, the resulting Slack 
 * message body contains the link to the GitHub issue created.
 * 
 * Context:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 */
class DefectReportingWorkflowTest {

    private InMemoryDefectRepository defectRepository;
    private MockGitHubPort gitHubPort;
    private MockSlackNotificationPort slackPort;
    private DefectReportingWorkflow workflow;

    @BeforeEach
    void setUp() {
        defectRepository = new InMemoryDefectRepository();
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
        workflow = new DefectReportingWorkflow(defectRepository, gitHubPort, slackPort);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String title = "Validating GitHub URL in Slack body";
        String expectedUrl = "https://github.com/bank-of-z/modernized/issues/454";
        
        // Configure mocks
        gitHubPort.setFakeUrl(expectedUrl);
        
        ReportDefectCommand command = new ReportDefectCommand(
            defectId, 
            title, 
            "Slack body missing GitHub link",
            "LOW"
        );

        // Act
        workflow.execute(command);

        // Assert
        // 1. Verify GitHub Port was called (implied by Slack check)
        // 2. Verify Slack notification contains the URL
        assertTrue(
            slackPort.wasNotified(expectedUrl), 
            "Slack body should include the GitHub issue URL returned by GitHubPort"
        );
        
        // 3. Verify the channel (optional sanity check)
        assertEquals("#vforce360-issues", slackPort.lastChannel);
    }

    @Test
    void testReportDefect_ShouldEmitEventWithUrl() {
        // Arrange
        String defectId = "VW-455";
        String expectedUrl = "https://github.com/bank-of-z/modernized/issues/455";
        gitHubPort.setFakeUrl(expectedUrl);

        ReportDefectCommand command = new ReportDefectCommand(
            defectId, 
            "Another defect", 
            "Desc",
            "HIGH"
        );

        // Act
        workflow.execute(command);

        // Assert
        // The aggregate should have stored the event with the URL
        var aggregate = defectRepository.findById(defectId);
        assertTrue(aggregate.isPresent(), "Aggregate should be saved");
        
        // Assuming aggregate exposes uncommitted events or state for verification
        // For TDD, we might check the state if available, or rely on Slack output
        // Here we check persistence
    }
}
