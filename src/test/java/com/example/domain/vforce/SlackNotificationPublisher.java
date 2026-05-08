package com.example.domain.vforce;

import com.example.domain.vforce.model.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackWebhookPort;

/**
 * Domain Service responsible for taking a defect report and ensuring it is
 * visible to the team. It coordinates creating the GitHub issue and then
 * notifying Slack with the resulting link.
 *
 * TDD Red Phase: This file contains the minimal structure to compile,
 * but the implementation logic is stubbed or incorrect to force test failure.
 */
public class SlackNotificationPublisher {

    private final GitHubPort gitHubPort;
    private final SlackWebhookPort slackPort;

    public SlackNotificationPublisher(GitHubPort gitHubPort, SlackWebhookPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void publishDefectNotification(ReportDefectCommand cmd) {
        // TDD RED PHASE: INTENTIONAL BUG / MISSING IMPLEMENTATION
        // We do not yet format the body to include the GitHub URL.
        // This causes testSlackBodyContainsGitHubLinkAfterDefectReported to fail.

        String url = gitHubPort.createIssue(cmd.title(), cmd.description());
        if (url == null) throw new RuntimeException("GitHub URL unavailable");

        // BAD: We currently just send a "Hello" text, missing the URL.
        // The test expects the URL to be in the body.
        String slackBody = "Hello, a defect was reported."; 

        slackPort.send(slackBody);
    }
}
