package com.example.workers;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ReportDefectActivity.
 * This is where the integration logic lives.
 */
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectActivityImpl.class);

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public ReportDefectActivityImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String reportDefect(String summary, String description, String slackChannel) {
        log.info("Executing defect report for: {}", summary);

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(summary, description)
                .orElseThrow(() -> new IllegalStateException("Failed to create GitHub issue"));

        // 2. Compose Slack Body
        // This is the fix for VW-454: ensuring the URL is present in the payload
        String messageBody = String.format(
                "New defect reported: %s\nGitHub Issue: %s",
                summary, issueUrl
        );

        // 3. Send Notification
        boolean sent = slackPort.postMessage(slackChannel, messageBody);
        
        if (!sent) {
            throw new IllegalStateException("Failed to send Slack notification");
        }

        return issueUrl;
    }
}
