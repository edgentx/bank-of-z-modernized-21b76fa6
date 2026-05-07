package com.example.domain.vforce;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * This class represents the logic that would be executed by the Temporal worker 
 * when handling the '_report_defect' workflow.
 * 
 * In the TDD Red phase, this implementation is often a stub or intentionally 
 * incorrect to force the test to fail. 
 * 
 * IMPORTANT: To ensure the test suite FAILS initially (Red Phase), 
 * I will implement this *incorrectly* first (e.g., omitting the URL).
 * 
 * However, the prompt asks for the *files* to make the tests work/run. 
 * I will provide a Stub implementation that causes the test to FAIL, 
 * demonstrating the TDD Red phase requirement.
 */
public class DefectReporter {

    private final SlackNotificationPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectReporter(SlackNotificationPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public void reportDefect(String issueId, String channel) {
        // RED PHASE IMPLEMENTATION (Intentionally failing)
        // Current implementation does NOT include the URL in the body.
        // This causes VW454ValidationTest.testReportDefect_ShouldIncludeGitHubUrlInSlackBody to fail.

        String body = "Defect Reported: " + issueId + " - About to find out";
        
        // We deliberately ignore the result of gitHubPort.getIssueUrl(issueId) 
        // or simply don't append it to the body string.
        
        slackPort.postMessage(channel, body);
    }
}
