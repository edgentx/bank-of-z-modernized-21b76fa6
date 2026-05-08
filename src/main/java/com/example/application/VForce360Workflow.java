package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.domain.vforce360.model.ReportDefectCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Workflow service for VForce360 operations.
 * Orchestrates the creation of GitHub issues and Slack notifications.
 * This class represents the 'Temporal-worker' logic mentioned in the defect report.
 */
@Service
public class VForce360Workflow {

    private static final Logger logger = LoggerFactory.getLogger(VForce360Workflow.class);
    private static final String DEFAULT_CHANNEL = "#vforce360-issues";

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public VForce360Workflow(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the defect reporting workflow.
     * 1. Creates a GitHub issue.
     * 2. Posts a notification to Slack containing the GitHub URL.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        logger.info("Executing report_defect for {}", cmd.defectId());

        // 1. Create GitHub Issue
        String issueUrl = githubPort.createIssue(cmd.repository(), cmd.title(), cmd.body());
        logger.info("GitHub Issue created: {}", issueUrl);

        // 2. Construct Slack Body with GitHub URL
        // Fixes S-FB-1 / VW-454: Ensure URL is present
        StringBuilder slackBody = new StringBuilder();
        slackBody.append("Defect Reported: ").append(cmd.title()).append("\n");
        slackBody.append("GitHub Issue: ").append(issueUrl).append("\n");

        // 3. Post to Slack
        slackPort.postMessage(DEFAULT_CHANNEL, slackBody.toString());
        logger.info("Slack notification sent to {}", DEFAULT_CHANNEL);
    }
}
