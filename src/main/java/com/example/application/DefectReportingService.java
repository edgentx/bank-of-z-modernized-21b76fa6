package com.example.application;

import com.example.domain.defects.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service implementation for reporting defects.
 * This service orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 * 
 * <p>This class acts as the implementation for the E2E test {@code VW454_GitHubUrlSlackValidationTest}.
 * The critical logic involves capturing the URL returned by the {@link GitHubPort} and ensuring
 * it is embedded within the message body sent to the {@link SlackNotificationPort}.</p>
 *
 * <p>Fixes defect VW-454.</p>
 */
@Service
public class DefectReportingService {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackPort;

    /**
     * Constructor for dependency injection.
     *
     * @param githubPort The port adapter for GitHub interactions.
     * @param slackPort The port adapter for Slack interactions.
     */
    public DefectReportingService(GitHubPort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the defect reporting workflow.
     * 1. Creates an issue on GitHub.
     * 2. Posts a notification to Slack containing the link to the created issue.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // Step 1: Create the issue on GitHub using the port
        // We assume the title and description from the command are sufficient.
        // In a more complex scenario, we might format the body to include metadata.
        String issueUrl = githubPort.createIssue(cmd.title(), cmd.description());

        // Step 2: Construct the Slack message body.
        // FIX for VW-454: We must explicitly include the issueUrl in the message.
        // Slack formatting allows <URL|Text> or just <URL> to create a hyperlink.
        String slackBody = String.format(
                "Defect Reported: %s%nSeverity: %s%nGitHub Issue: <%s|View Issue>",
                cmd.title(),
                cmd.severity(),
                issueUrl
        );

        // Step 3: Post the notification to the target channel.
        // The channel ID could be configurable, but hardcoded here as per test setup.
        slackPort.postMessage("#vforce360-issues", slackBody);
    }
}
