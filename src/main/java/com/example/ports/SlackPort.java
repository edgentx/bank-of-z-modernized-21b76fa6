package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * This isolates the domain logic from the specific Slack API client library.
 */
public interface SlackPort {

    /**
     * Sends a notification about a defect.
     *
     * @param defectId     The ID of the defect (e.g., "VW-454").
     * @param summary      A short summary of the defect.
     * @param githubIssueId The ID of the GitHub issue associated with the defect.
     */
    void sendDefectNotification(String defectId, String summary, String githubIssueId);
}