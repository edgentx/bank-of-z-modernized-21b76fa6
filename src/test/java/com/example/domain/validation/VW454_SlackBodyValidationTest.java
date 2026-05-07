package com.example.domain.validation;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotifierPort;
import com.example.mocks.InMemoryGitHubRepository;
import com.example.mocks.SpySlackNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for VW-454.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * 
 * Context:
 * Triggered via temporal-worker exec (_report_defect).
 * The system must create a GitHub issue and post a notification to Slack.
 * The Slack message body MUST include the GitHub issue URL.
 */
public class VW454_SlackBodyValidationTest {

    /**
     * Simulates the temporal worker logic or service that handles defect reporting.
     * In a real app, this might be injected or instantiated, but for unit testing
     * the component logic, we construct it with ports.
     */
    private DefectReportService createService(GitHubRepositoryPort gitHub, SlackNotifierPort slack) {
        return new DefectReportService(gitHub, slack);
    }

    @Test
    @DisplayName("GIVEN defect report command WHEN executing THEN Slack body contains GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // Arrange
        // 1. Setup Mocks
        var mockGitHub = new InMemoryGitHubRepository();
        var spySlack = new SpySlackNotifier();

        // 2. Configure Mock Behavior
        // The GitHub port returns a specific valid URL when "createIssue" is called.
        String expectedUrl = "https://github.com/project-21b76fa6/issues/454";
        mockGitHub.setNextIssueUrl(expectedUrl);

        // 3. Instantiate the System Under Test (SUT)
        DefectReportService service = createService(mockGitHub, spySlack);

        // 4. Define the input command
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454: GitHub URL in Slack body",
            "Severity: LOW. End-to-end validation failed.",
            "#vforce360-issues"
        );

        // Act
        // Execute the workflow logic (red phase: this class might not exist yet)
        try {
            service.execute(cmd);
        } catch (Exception e) {
            // In TDD Red phase, we expect this might fail or throw, 
            // but we primarily want to assert the state/interactions if it partially works.
            // However, for the purpose of this test file, we assume the structure exists
            // enough to call it. If it doesn't compile, that is also "Red".
        }

        // Assert
        // The core validation: Check that the message received by Slack contains the URL.
        String actualSlackMessage = spySlack.getLastMessageBody();
        String actualChannel = spySlack.getLastChannel();

        // 1. Verify it went to the right place
        assertEquals("#vforce360-issues", actualChannel, "Slack should post to the specific issues channel");

        // 2. Verify the content (The VW-454 Requirement)
        assertNotNull(actualSlackMessage, "Slack message body should not be null");
        assertTrue(
            actualSlackMessage.contains(expectedUrl), 
            "Slack body MUST include GitHub issue URL. Expected to contain: " + expectedUrl
        );
    }

    @Test
    @DisplayName("GIVEN GitHub service fails WHEN reporting defect THEN appropriate error handling occurs (Regression)")
    public void testGitHubFailureGracefulDegradation() {
        // Arrange
        var mockGitHub = new InMemoryGitHubRepository();
        mockGitHub.setFailureMode(true); // Simulate GitHub outage
        var spySlack = new SpySlackNotifier();
        
        DefectReportService service = createService(mockGitHub, spySlack);
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454-Fail: Test Failure",
            "Test failure scenario",
            "#vforce360-issues"
        );

        // Act & Assert
        // We expect the system to handle the failure (maybe logging to Slack without the URL)
        assertThrows(RuntimeException.class, () -> {
            service.execute(cmd);
        });
        
        // Verify that a message was still attempted, perhaps with a generic error indicator
        assertTrue(spySlack.wasCalled(), "Slack should still be notified even if GitHub fails");
    }
}
