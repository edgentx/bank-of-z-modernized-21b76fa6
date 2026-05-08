package com.example.domain.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service orchestrating the defect reporting workflow.
 * This acts as the Workflow/Activity implementation in the Temporal context.
 */
@Service
public class DefectReportService {

    private static final Logger logger = Logger.getLogger(DefectReportService.class.getName());

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the report_defect workflow.
     * 1. Create Issue on GitHub.
     * 2. Notify Slack with result.
     *
     * @param title The defect title.
     * @param description The defect description.
     * @param channel The Slack channel to notify.
     */
    public void reportDefect(String title, String description, String channel) {
        logger.info("Reporting defect: " + title);

        // Step 1: Create GitHub Issue
        Optional<String> issueUrl = gitHubPort.createIssue(title, description);

        // Step 2: Construct Slack Body
        // FIX for VW-454: Ensure the URL is actually appended to the body
        StringBuilder slackBody = new StringBuilder("Defect Reported: ");
        slackBody.append(title).append("\n");

        if (issueUrl.isPresent()) {
            String url = issueUrl.get();
            slackBody.append("GitHub Issue: ").append(url);
            logger.info("Created issue: " + url);
        } else {
            slackBody.append("Failed to create GitHub issue.");
            logger.warning("Failed to create GitHub issue for: " + title);
        }

        // Step 3: Send Notification
        slackPort.sendMessage(channel, slackBody.toString());
    }
}
