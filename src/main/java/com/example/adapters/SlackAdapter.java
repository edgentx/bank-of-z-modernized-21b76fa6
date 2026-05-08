package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackPort.
 * In a production environment, this would use a WebClient to call the Slack API.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void sendMessage(String channel, String messageBody) {
        // Implementation Note: This is a stub for the real adapter logic.
        // Actual implementation would involve:
        // 1. Constructing a JSON payload for the Slack Webhook or API chat.postMessage.
        // 2. POSTing to the Slack endpoint.
        // 3. Handling 200 OK vs error responses.
        
        // Logging the action (simulated)
        System.out.println("[SlackAdapter] Sending to channel " + channel + ": " + messageBody);
    }
}