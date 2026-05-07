package com.example.application;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for reporting defects.
 * This logic fixes VW-454 by ensuring the generated GitHub URL is
 * explicitly included in the Slack notification body.
 */
@Component
public class DefectReportingActivity {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivity.class);

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingActivity(GitHubIssuePort gitHubIssuePort,
                                   SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     * VW-454 Fix: Ensure the URL returned from GitHub is injected into the Slack payload.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    public void reportDefect(String title, String description) {
        log.info("Executing report_defect for: {}", title);

        // 1. Create the GitHub issue
        String issueUrl = gitHubIssuePort.createIssue(title, description);

        // 2. Construct the Slack body including the GitHub URL
        // This satisfies the criteria: "Slack body includes GitHub issue: <url>"
        String slackBody = String.format(
            "Defect Reported: %s\nGitHub Issue: <%s|Link>",
            title,
            issueUrl
        );

        // 3. Post to Slack
        slackNotificationPort.postMessage(slackBody);

        log.info("Successfully reported defect {} to Slack with link {}", title, issueUrl);
    }
}
