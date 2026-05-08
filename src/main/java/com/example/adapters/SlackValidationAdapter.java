package com.example.adapters;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Adapter responsible for handling defect reporting workflows.
 * It coordinates creating a GitHub issue and then notifying Slack with the details.
 * This implementation addresses the VW-454 defect regarding URL formatting.
 */
public class SlackValidationAdapter {

    private static final Logger log = LoggerFactory.getLogger(SlackValidationAdapter.class);
    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public SlackValidationAdapter(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Handles the ReportDefectCommand by creating a GitHub issue and sending a notification to Slack.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        log.info("Reporting defect: {} for project: {}", cmd.title(), cmd.projectId());

        // Step 1: Create the GitHub issue
        // The body combines the description and the reproduction steps context if available
        String issueBody = String.format("**Description:** %s\n**Component:** %s", cmd.description(), cmd.component());
        
        String githubUrl = gitHubPort.createIssue(
            cmd.title(),
            issueBody,
            cmd.projectId(), // Used as a label
            cmd.severity()    // Used as a label
        );

        // Step 2: Notify Slack
        // The body must include the GitHub URL to satisfy VW-454 acceptance criteria.
        String slackMessage = formatSlackMessage(cmd, githubUrl);
        
        slackPort.sendMessage(Map.of("text", slackMessage));
    }

    /**
     * Formats the Slack message payload.
     * Ensures the GitHub URL is correctly embedded in the text.
     */
    private String formatSlackMessage(ReportDefectCmd cmd, String githubUrl) {
        return String.format(
            "New Defect Reported: %s\n" +
            "Project: %s\n" +
            "Severity: %s\n" +
            "Component: %s\n" +
            "GitHub Issue: %s",
            cmd.title(),
            cmd.projectId(),
            cmd.severity(),
            cmd.component(),
            githubUrl
        );
    }
}
