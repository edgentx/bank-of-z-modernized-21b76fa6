package com.example.vforce.slack;

import com.example.ports.GithubPort;
import com.example.ports.SlackPort;
import com.example.vforce.github.GithubIssue;
import com.example.vforce.shared.ReportDefectCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service handling the logic of reporting defects.
 * 1. Creates a GitHub issue.
 * 2. Posts a notification to Slack.
 *
 * Critical for S-FB-1: Ensures the GitHub URL is propagated to the Slack body.
 */
public class SlackNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);
    private final SlackPort slackPort;
    private final GithubPort githubPort;

    public SlackNotificationService(SlackPort slackPort, GithubPort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    public void handleReportDefect(ReportDefectCommand cmd) {
        log.info("Handling defect report: {}", cmd.summary());

        // 1. Attempt to create the GitHub issue
        Optional<GithubIssue> issue = githubPort.createIssue(cmd);

        // 2. Construct the Slack message
        // The defect VW-454 requires the URL to be present in the body.
        final String message;
        if (issue.isPresent()) {
            GithubIssue ghIssue = issue.get();
            message = "Defect Reported: " + cmd.summary() + "\nGitHub issue: " + ghIssue.url();
        } else {
            message = "Defect Reported (GitHub creation failed): " + cmd.summary();
        }

        // 3. Send the notification
        slackPort.sendMessage(message);
    }
}
