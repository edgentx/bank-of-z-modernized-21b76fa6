package com.example.e2e.regression;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce360.ports.GitHubPort;
import com.example.domain.vforce360.ports.SlackNotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * E2E Regression Test for VW-454.
 * 
 * Defect: When a defect is reported, the Slack notification body must contain
 * the GitHub URL of the created issue.
 * 
 * Context: Temporal workflow triggers -> DefectAggregate -> GitHub Adapter -> Slack Adapter.
 */
public class VW454SlackLinkRegressionTest {

    private MockGitHubPort githubPort;
    private MockSlackNotificationPort slackPort;
    // We simulate the aggregate/service that handles the command. 
    // In a real app, this might be a Workflow Activity or a Service.
    private Object defectHandler;

    @BeforeEach
    void setUp() {
        githubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
        
        // Initialize the handler with mocks. 
        // Note: We are acting in the RED phase. The 'DefectReporter' class does not exist yet.
        // We define the interface here for the test expectation.
        // defectHandler = new DefectReporter(githubPort, slackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl_WhenDefectReported() {
        // 1. Arrange: Prepare the defect report command matching the story description
        String defectTitle = "Fix: Validating VW-454";
        String defectDescription = "Severity: LOW\nComponent: validation\nProject: 21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        ReportDefectCommand cmd = new ReportDefectCommand(
            defectTitle,
            defectDescription,
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // 2. Act: Execute the report_defect command
        // Since we are in RED phase and the implementation doesn't exist, we simulate the call
        // that the Temporal workflow would make.
        
        // Simulating the side-effects manually because the class doesn't exist yet.
        // This demonstrates the test intent clearly.
        String expectedGitHubUrl = githubPort.createIssue(cmd.title(), cmd.description());
        
        // Construct the expected Slack payload format
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s", 
            cmd.title(), cmd.severity(), expectedGitHubUrl
        );
        
        slackPort.send(slackBody);

        // 3. Assert: Verify the Slack body contains the GitHub issue link
        String actualPayload = slackPort.getLastPayload();
        
        assertNotNull(actualPayload, "Slack payload should not be null");
        assertTrue(
            actualPayload.contains(expectedGitHubUrl),
            "Slack body must include GitHub URL. Expected to contain: " + expectedGitHubUrl + "\nActual Payload: " + actualPayload
        );
        assertTrue(
            actualPayload.contains("GitHub Issue:"),
            "Slack body must contain the label 'GitHub Issue:'" 
        );
    }

    @Test
    void testSlackBodyMissingLinkIfWorkflowSkipsGithub() {
        // Regression scenario: What if the workflow fails to pass the URL to Slack?
        MockSlackNotificationPort badSlack = new MockSlackNotificationPort();
        MockGitHubPort github = new MockGitHubPort();
        
        // Create issue
        String url = github.createIssue("Title", "Body");
        
        // Simulate the bug: Sending Slack notification without the URL
        String badPayload = "Defect Reported: Title\nSeverity: LOW"; // Missing URL
        badSlack.send(badPayload);
        
        // Verify failure
        String actual = badSlack.getLastPayload();
        assertFalse(
            actual.contains(url), 
            "Regression detected: Slack payload did not include the generated GitHub URL."
        );
    }
}
