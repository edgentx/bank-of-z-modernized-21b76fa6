package com.example.domain.vforce;

import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;

/**
 * Domain Service responsible for taking a defect report and ensuring it is
 * visible to the team. It coordinates creating the GitHub issue and then
 * notifying Slack with the resulting link.
 *
 * FIXED: S-FB-1: Now correctly formats the GitHub URL into the Slack notification body.
 */
public class SlackNotificationPublisher {

    private final GitHubPort gitHubPort;
    private final SlackWebhookPort slackPort;

    public SlackNotificationPublisher(GitHubPort gitHubPort, SlackWebhookPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void publishDefectNotification(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        String url = gitHubPort.createIssue(cmd.title(), cmd.description());
        
        if (url == null) {
            throw new RuntimeException("GitHub URL unavailable");
        }

        // 2. Construct Slack Message with GitHub Link
        // Using Slack link formatting: <URL|text> or just <URL>
        // S-FB-1 Fix: Ensure URL is present in the body.
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nComponent: %s\nGitHub Issue: <%s|View Issue>",
            cmd.title(),
            cmd.severity(),
            cmd.component(),
            url
        );

        // 3. Send Notification
        slackPort.send(slackBody);
    }
}
