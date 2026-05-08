package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects to external systems.
 * Orchestrates the lookup of GitHub issue URLs and the posting of notifications to Slack.
 * <p>
 * This is the System Under Test (SUT) for the VW-454 regression.
 * It corrects the defect where the GitHub URL was omitted from the Slack message body.
 * </p>
 */
public class DefectReportingWorkflow {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort githubPort;

    /**
     * Constructor for dependency injection.
     *
     * @param slackPort The port for Slack operations.
     * @param githubPort The port for GitHub operations.
     */
    public DefectReportingWorkflow(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Reports a defect by constructing a message containing the issue details and
     * the direct GitHub URL, then posting it to the specified Slack channel.
     *
     * @param channel     The target Slack channel (e.g., "#vforce360-issues").
     * @param issueId     The unique identifier of the issue (e.g., "VW-454").
     * @param description A human-readable description of the defect.
     */
    public void reportDefect(String channel, String issueId, String description) {
        // 1. Retrieve the specific URL for this issue from the GitHub port
        String issueUrl = githubPort.getIssueUrl(issueId);

        // 2. Construct the message body including the URL
        // This ensures the regression test VW-454 passes.
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Reported: ").append(description).append("\n");
        messageBody.append("GitHub Issue: <").append(issueUrl).append(">");

        // 3. Post the constructed message to Slack
        slackPort.postMessage(channel, messageBody.toString());
    }
}
