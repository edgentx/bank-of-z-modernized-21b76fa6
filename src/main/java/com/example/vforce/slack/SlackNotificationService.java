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
        // RED PHASE IMPLEMENTATION STUB
        // This code is intentionally minimal or incorrect to fail the tests.
        // The developer will fix this in the Green phase.

        Optional<GithubIssue> issue = githubPort.createIssue(cmd);

        String message;
        if (issue.isPresent()) {
            // The defect VW-454 is likely that this line was missing or incorrect.
            // Currently failing: "GitHub issue: <url>"
            message = "Defect Reported: " + cmd.summary();
        } else {
            message = "Defect Reported (GitHub creation failed): " + cmd.summary();
        }

        slackPort.sendMessage(message);
    }
}
