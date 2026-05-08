package com.example.adapters;

import com.example.ports.SackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Real implementation of SlackPort using Slack SDK (Simulated for this exercise).
 * In a production environment, this would inject a WebClient or SlackClient.
 */
@Component
public class RealSlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackAdapter.class);

    @Override
    public void sendNotification(String channel, String body, Map<String, String> contextMap) {
        // Implementation stub for the 'Real' adapter.
        // In the Green phase, the domain logic relies on the interface.
        // The actual HTTP call to Slack API would go here.
        log.info("[RealSlack] Sending to {}: {}", channel, body);
    }
}
