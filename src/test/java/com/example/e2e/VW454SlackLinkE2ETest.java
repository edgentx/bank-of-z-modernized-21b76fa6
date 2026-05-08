package com.example.e2e;

import com.example.domain.shared.Command;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * 
 * Story: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Defect: Reported defect where the Slack notification body for a reported issue
 * was expected to contain the GitHub URL but might be missing.
 * 
 * Expected Behavior:
 * 1. Trigger _report_defect via temporal-worker exec (simulated via Command).
 * 2. Verify Slack body contains GitHub issue link.
 * 
 * Phase: RED (Tests written before implementation exists).
 */
class VW454SlackLinkE2ETest {

    // Dependencies (Mocks)
    private InMemorySlackNotificationPort slackMock;
    private InMemoryGitHubIssuePort githubMock;

    // System Under Test (SUT)
    // We assume the existence of a service/handler that processes defect reports.
    // Since this is the Red phase, this class does not yet exist.
    private DefectReportService defectReportService; 

    @BeforeEach
    void setUp() {
        // Initialize mocks with expected defaults
        slackMock = new InMemorySlackNotificationPort();
        githubMock = new InMemoryGitHubIssuePort("https://github.com/test-org");

        // Inject mocks into the service (Dependency Injection pattern)
        // This constructor will not exist until implementation phase.
        defectReportService = new DefectReportService(slackMock, githubMock);
    }

    @Test
    void shouldContainGitHubUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String expectedChannel = "#vforce360-issues";
        String defectTitle = "VW-454: Validation Failure";
        String defectDescription = "Critical validation logic failed in module X.";

        // Act
        // Trigger the workflow logic. This mimics the temporal-worker executing the logic.
        defectReportService.reportDefect(new ReportDefectCommand(
            expectedChannel, 
            defectTitle, 
            defectDescription
        ));

        // Assert
        // 1. Verify an issue was created in GitHub (implicitly checking the mock state if needed, but primarily for URL generation)
        // 2. Verify the Slack message was posted
        InMemorySlackNotificationPort.PostedMessage postedMessage = slackMock.findFirstByChannel(expectedChannel);
        
        assertNotNull(postedMessage, "Slack message should have been posted to " + expectedChannel);
        
        // 3. CRITICAL ASSERTION: The body must contain the GitHub URL
        // We retrieve the expected URL from the GitHub mock state to ensure exact match.
        String expectedGitHubUrl = githubMock.createIssue(defectTitle, defectDescription); // Re-generate the expected URL
        
        assertTrue(
            postedMessage.body.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub URL.\nExpected to contain: " + expectedGitHubUrl + "\nActual Body: " + postedMessage.body
        );

        // 4. Verify the link format is correct (e.g. slack link formatting <url|text> or just url)
        // Depending on requirements, we might just check for the raw URL presence.
        assertFalse(postedMessage.body.isBlank(), "Slack body should not be blank");
    }

    // --- Inner Classes for Test Context (These simulate the Command/Service structure we expect) ---

    /**
     * Command representing the input to the system.
     * This corresponds to 'Trigger _report_defect via temporal-worker exec'.
     */
    private static class ReportDefectCommand implements Command {
        private final String channel;
        private final String title;
        private final String description;

        public ReportDefectCommand(String channel, String title, String description) {
            this.channel = channel;
            this.title = title;
            this.description = description;
        }

        public String getChannel() { return channel; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    /**
     * Service Stub.
     * This class WILL NOT COMPILE in the current repo state (Red Phase).
     * It represents the logic that needs to be written to pass the test.
     */
    private static class DefectReportService {
        private final SlackNotificationPort slackPort;
        private final GitHubIssuePort githubPort;

        public DefectReportService(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
            this.slackPort = slackPort;
            this.githubPort = githubPort;
        }

        public void reportDefect(ReportDefectCommand cmd) {
            // Implementation goes here in Green phase.
            // Currently, this body is empty or throws UnsupportedOperationException to ensure the test fails initially.
            // To ensure a "meaningful" red phase, we might throw a specific exception or return void.
            // If we leave it empty, the assertion 'slackMock.findFirstByChannel' will fail because no message was sent.
            throw new UnsupportedOperationException("DefectReportService not implemented yet");
        }
    }
}
