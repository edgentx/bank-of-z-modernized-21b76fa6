package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackPort.
 * In a real environment, this would use a Slack WebClient or an API client.
 * For the scope of this defect fix (validation of content), we simulate the send
 * to allow the system to run end-to-end locally without a live Slack token.
 */
@Component
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String message) {
        // In production: SlackClient.postMessage(message);
        // Here we log to standard out so the CI logs capture the "Body"
        log.info("[SLACK ADAPTER] Sending message: {}", message);
        
        // Simulate network latency
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}