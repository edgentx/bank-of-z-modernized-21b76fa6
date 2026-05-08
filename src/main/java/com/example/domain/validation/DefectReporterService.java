package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for reporting defects to GitHub and notifying Slack.
 * This acts as the orchestrator that fixes defect VW-454 by ensuring the URL
 * is passed to the Slack body generation.
 */
@Service
public class DefectReporterService {

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    /**
     * Constructor injection for dependencies.
     * @param githubPort The adapter for GitHub interactions.
     * @param slackPort The adapter for Slack interactions.
     */
    public DefectReporterService(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect by creating an issue on GitHub and posting a notification to Slack.
     * 
     * @param title The title of the defect.
     * @param description The description/body of the defect.
     */
    public void reportDefect(String title, String description) {
        // Step 1: Create GitHub Issue
        String issueUrl = githubPort.createDefect(title, description);

        // Step 2: Compose Slack Message Body
        // FIX for VW-454: Ensure the issueUrl is explicitly included and formatted for unfurling.
        String slackBody = buildSlackBody(title, issueUrl);

        // Step 3: Send Notification
        slackPort.sendMessage("#vforce360-issues", slackBody);
    }

    /**
     * Helper to format the Slack message.
     * Wraps the URL in < > to force Slack to unfurl it.
     */
    private String buildSlackBody(String title, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("*New Defect Reported*\n");
        sb.append("*Title:* ").append(title).append("\n");
        sb.append("*GitHub Issue:* <").append(url).append("|View Issue>\n");
        return sb.toString();
    }
}
