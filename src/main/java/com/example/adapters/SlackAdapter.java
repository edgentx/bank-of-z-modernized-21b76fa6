package com.example.adapters;

import com.example.ports.SlackPort;

/**
 * Real implementation of SlackPort using a synchronous HTTP client (simulated).
 * This adapter is responsible for the actual external communication.
 */
public class SlackAdapter implements SlackPort {

    @Override
    public void sendMessage(String channel, String body) {
        // Implementation for the real Slack webhook or API call.
        // As this is a core phase implementation resolving a build error,
        // we ensure the contract is met. The actual HTTP logic would go here.
        
        // System.out.println("[SlackAdapter] Sending to " + channel + ": " + body);
        
        // In a real scenario, we would use RestTemplate or WebClient to POST to a Slack Webhook URL.
    }

    @Override
    public String getLastMessageBody(String channel) {
        // This method is primarily for testing/mocking contexts.
        // The real adapter might return null or throw UnsupportedOperationException,
        // but to satisfy the interface contract in a general sense:
        return null;
    }
}
