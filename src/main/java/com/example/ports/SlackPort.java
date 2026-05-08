package com.example.ports;

/**
 * Port interface for Slack notification operations.
 * Acts as a bridge between the domain logic and the external Slack infrastructure.
 */
public interface SlackPort {
    /**
     * Sends a notification to Slack with the given message and associated GitHub URL.
     *
     * @param message   The defect message.
     * @param githubUrl The URL to the GitHub issue.
     * @return The formatted body of the Slack message sent.
     */
    String postDefectNotification(String message, String githubUrl);
}
