package com.example.domain.vforce360;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Placeholder class representing the Workflow/Activity implementation 
 * that would be invoked by the Temporal worker.
 * 
 * In the Red Phase, this class is intentionally stubbed or missing the logic,
 * causing the tests to fail until implementation is provided.
 */
public class DefectReportWorkflow {

    private final GitHubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportWorkflow(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(String title, String description) {
        // RED PHASE: 
        // Current implementation is intentionally incomplete or broken 
        // to satisfy the TDD Red Phase requirement.
        // 
        // Missing logic:
        // 1. Create GitHub issue via githubPort
        // 2. Append URL to message body
        // 3. Send via slackPort
        
        // Example of broken behavior for Red Phase:
        // slackPort.postMessage("#vforce360-issues", "Defect reported: " + title); 
        // (The URL is missing, causing the test to fail)
        
        throw new UnsupportedOperationException("Implement reportDefect logic to satisfy VW-454");
    }
}
