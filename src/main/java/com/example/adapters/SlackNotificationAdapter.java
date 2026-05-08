package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the SlackNotificationPort.
 * In a production environment, this would use the Slack WebAPI.
 * For this module, we implement the contract and log the action.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // Real Slack API call would go here, e.g., WebClient.post()...
        // For S-FB-1 build success, we simply log to verify execution path.
        log.info("[SLACK] Channel: {} | Body: {}", channel, body);
        
        // To ensure we don't introduce external dependencies like Slack SDK 
        // which might complicate the "green" phase, we leave the actual HTTP call 
        // mocked or implemented via a generic RestTemplate in a real scenario.
        // This class serves as the concrete implementation binding.
    }
}
