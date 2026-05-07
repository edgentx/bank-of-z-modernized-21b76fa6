package com.example.domain.validation;

import com.example.ports.GitHubRepositoryPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic of reporting defects.
 * Orchestrates interactions between GitHub (creation) and Slack (notification).
 */
@Service
public class DefectReportService {

    private final GitHubRepositoryPort gitHub;
    private final SlackNotifierPort slack;

    /**
     * Constructor for Dependency Injection.
     * Spring will automatically inject the configured beans (Adapters) implementing these ports.
     *
     * @param gitHub The GitHub adapter.
     * @param slack The Slack adapter.
     */
    public DefectReportService(GitHubRepositoryPort gitHub, SlackNotifierPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting workflow.
     * 1. Create Issue in GitHub.
     * 2. Construct notification body including the URL.
     * 3. Send to Slack.
     *
     * @param cmd The command containing title, description, and target channel.
     */
    public void execute(ReportDefectCommand cmd) {
        // 1. Create GitHub Issue
        String issueUrl = gitHub.createIssue(cmd.title(), cmd.description());

        // 2. Construct Slack Message (Bug Fix: Ensure URL is included)
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("*Defect Report Created*\n");
        messageBuilder.append("*Title:*").append(cmd.title()).append("\n");
        messageBuilder.append("*GitHub Issue: <").append(issueUrl).append("|View Details>");

        // 3. Send to Slack
        slack.send(cmd.slackChannel(), messageBuilder.toString());
    }
}
