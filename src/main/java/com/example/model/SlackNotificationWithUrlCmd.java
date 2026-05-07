package com.example.model;

import com.example.domain.shared.Command;

/**
 * Command carrying the GitHub URL to be posted to Slack.
 */
public record SlackNotificationWithUrlCmd(
        String defectId,
        String githubIssueUrl,
        String severity
) implements Command {}
