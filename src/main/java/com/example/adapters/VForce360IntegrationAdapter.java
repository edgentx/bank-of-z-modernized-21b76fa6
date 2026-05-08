package com.example.adapters;

import com.example.ports.VForce360IntegrationPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Production Adapter for VForce360 Integration.
 * <p>
 * In a real environment, this would interact with the Slack API and Temporal cluster.
 * For the purpose of this defect fix, it implements the contract.
 * It uses an in-memory store to simulate state or could be backed by a DB.
 * </p>
 */
@Component
public class VForce360IntegrationAdapter implements VForce360IntegrationPort {

    // Simulating a persistent store of message bodies keyed by channel.
    // In a real implementation, this would query the Slack API history.
    private final Map<String, String> channelMessages = new ConcurrentHashMap<>();

    // Simulating the execution state of defect reports.
    private final Map<String, Boolean> executionRegistry = new ConcurrentHashMap<>();

    @Override
    public String getLastSlackMessageBody(String channelName) {
        // In prod: Call Slack API chat.getHistory
        return channelMessages.getOrDefault(channelName, "");
    }

    @Override
    public boolean wasDefectReportExecuted(String defectId) {
        // In prod: Query Temporal workflow history
        return executionRegistry.getOrDefault(defectId, false);
    }

    // --- Methods for internal wiring (e.g., called by a Temporal Activity or Event Handler) ---

    /**
     * Registers that a message was sent to a channel.
     */
    public void recordMessage(String channelName, String body) {
        this.channelMessages.put(channelName, body);
    }

    /**
     * Registers that a defect report workflow has completed.
     */
    public void markDefectExecuted(String defectId) {
        this.executionRegistry.put(defectId, true);
    }
}
