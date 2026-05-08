package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Implementation.
 * This class is executed by the Temporal Worker to handle the logic
 * of reporting a defect. It acts as the glue between the Temporal
 * workflow trigger and the external ports.
 */
@Component
@ActivityImpl(taskQueue = "REPORT_DEFECT_TASK_QUEUE")
public class TemporalNotificationWorker {

    private static final Logger log = LoggerFactory.getLogger(TemporalNotificationWorker.class);

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubIssuePort gitHubIssuePort;

    /**
     * Constructor injection of Ports.
     * @param slackNotificationPort The adapter for sending Slack messages.
     * @param gitHubIssuePort The adapter for resolving GitHub URLs.
     */
    public TemporalNotificationWorker(SlackNotificationPort slackNotificationPort,
                                      GitHubIssuePort gitHubIssuePort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubIssuePort = gitHubIssuePort;
    }

    /**
     * Activity method exposed to Temporal.
     * Expected to be invoked via temporal-worker exec (as per defect reproduction).
     *
     * @param issueId The ID of the issue (e.g. "VW-454")
     */
    public void reportDefect(String issueId) {
        log.info("Executing report_defect activity for issue: {}", issueId);

        // 1. Resolve the GitHub URL using the port
        String githubUrl = gitHubIssuePort.getIssueUrl(issueId);

        // 2. Construct the message body
        // According to the test expectations, the body must explicitly contain the URL.
        String messageBody = "Defect reported: " + issueId + "\nGitHub Issue: " + githubUrl;

        // 3. Send the message to the specific channel
        String targetChannel = "#vforce360-issues";
        slackNotificationPort.sendMessage(targetChannel, messageBody);

        log.info("Successfully sent notification for {} to {}", issueId, targetChannel);
    }
}
