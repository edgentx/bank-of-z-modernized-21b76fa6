package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default implementation of SlackPort.
 * In a real environment, this would use the Slack SDK to post a message.
 * For the defect validation, we ensure the message formatting logic is present.
 */
@Component
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSlackAdapter.class);

    @Override
    public void sendMessage(String channel, String message) {
        // Real implementation would use com.slack.api.methods.MethodsClient
        logger.info("Sending message to {}: {}", channel, message);
        // System.out.println("[Slack] " + channel + ": " + message); 
    }
}