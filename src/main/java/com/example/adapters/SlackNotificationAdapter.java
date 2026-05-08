package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for Slack notifications.
 * In a production environment, this would use WebClient to call the Slack API.
 * 
 * Note: The `getLastMessageBody` method is not practically implemented in a real stateless
 * adapter (as it requests are fire-and-forget), but it is required by the port interface
 * for contract compatibility with testing mocks.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    
    // Field to store the last message strictly for interface compliance/simulation
    private volatile String lastSentMessage;

    @Override
    public void sendMessage(String messageBody) {
        this.lastSentMessage = messageBody;
        
        // Placeholder for actual Slack API logic (e.g., WebClient.post()...)
        // For now, we log to simulate the external interaction.
        log.info("[SLACK ADAPTER] Sending message: {}", messageBody);
    }

    @Override
    public String getLastMessageBody() {
        return this.lastSentMessage;
    }
}
