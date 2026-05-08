package com.example.mocks;

import com.example.ports.VForce360IntegrationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for VForce360 Integration.
 * Simulates Slack message history for regression testing.
 */
public class MockVForce360IntegrationAdapter implements VForce360IntegrationPort {

    private final Map<String, String> channelMessages = new HashMap<>();
    private boolean defectExecuted = false;

    /**
     * Sets the message body that should be returned for a specific channel.
     * Use this to configure the "Expected Behavior" scenario.
     *
     * @param channel The channel name.
     * @param body The message body content.
     */
    public void setSlackMessage(String channel, String body) {
        this.channelMessages.put(channel, body);
    }

    public void setDefectExecuted(boolean executed) {
        this.defectExecuted = executed;
    }

    @Override
    public String getLastSlackMessageBody(String channelName) {
        // Default to empty string to simulate "Actual Behavior" (missing link)
        return channelMessages.getOrDefault(channelName, "");
    }

    @Override
    public boolean wasDefectReportExecuted(String defectId) {
        return this.defectExecuted;
    }
}