package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Implementations must handle the actual HTTP posting to Slack webhooks.
 */
public interface SlackPort {

    /**
     * Notifies the Slack channel that a defect has been reported and filed on GitHub.
     *
     * @param defectId The internal defect ID (e.g., "VW-454")
     * @param githubIssueUrl The full URL to the created GitHub issue (e.g., "https://github.com/...")
     */
    void notifyDefectReported(String defectId, String githubIssueUrl);
}