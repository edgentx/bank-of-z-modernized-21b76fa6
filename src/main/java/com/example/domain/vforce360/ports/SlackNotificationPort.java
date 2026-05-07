package com.example.domain.vforce360.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the VForce360 defect reporting workflow.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The formatted JSON payload intended for Slack.
     * @return true if the API accepts the request, false otherwise.
     */
    boolean send(String payload);

    /**
     * Retrieves the last payload sent to the mock for verification in tests.
     * (This method is specific to the testing contract/mocks).
     */
    String getLastPayload();
}
