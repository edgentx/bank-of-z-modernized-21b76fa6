package com.vforce360.validation.ports;

/**
 * Payload object for Slack notifications.
 */
public class SlackMessagePayload {

    private final String body;

    public SlackMessagePayload(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
