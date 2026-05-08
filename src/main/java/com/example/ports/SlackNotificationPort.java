package com.example.ports;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Port for sending notifications to Slack.
 * Used by temporal workflows to report defects or status updates.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The Slack channel ID or name (e.g., "#vforce360-issues").
     * @param body The text content of the message.
     * @param contextMetadata A map of contextual data (e.g., githubUrl, issueId) to be embedded in the message.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean postMessage(String channel, String body, Map<String, String> contextMetadata);
}
