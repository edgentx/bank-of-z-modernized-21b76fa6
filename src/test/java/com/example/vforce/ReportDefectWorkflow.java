package com.example.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GithubIssuePort;

/**
 * Domain Workflow for reporting a defect.
 * This class represents the 'Temporal-worker' logic described in the story.
 * It orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 */
public class ReportDefectWorkflow {

    private final GithubIssuePort githubPort;
    private final SlackNotificationPort slackPort;

    public ReportDefectWorkflow(GithubIssuePort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void execute(ReportDefectCmd cmd) {
        // Implementation will be added in Green phase.
        // For now, this is just a shell to allow compilation of the test.
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public record ReportDefectCmd(
        String ticketId,
        String description,
        String severity
    ) {}
}
