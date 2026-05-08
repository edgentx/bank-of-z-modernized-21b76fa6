package com.example.ports;

/**
 * Port for formatting Slack messages related to defect reporting.
 */
public interface SlackMessageValidator {
    /**
     * Formats a Slack message body to include a GitHub issue link.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param issueTitle The title of the issue.
     * @param githubUrl The full URL to the GitHub issue.
     * @return The formatted Slack message string.
     */
    String formatSlackMessage(String defectId, String issueTitle, String githubUrl);
}
