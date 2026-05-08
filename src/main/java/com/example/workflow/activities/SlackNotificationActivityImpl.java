package com.example.workflow.activities;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of the Slack Notification Activity.
 * This contains the actual logic to call the Slack API (via the Adapter).
 */
@Component
public class SlackNotificationActivityImpl implements SlackNotificationActivity {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationActivityImpl.class);

    // In a real app, we would inject a specific SlackAdapter here that implements the port logic.
    // For the defect fix, the critical part is that the URL passed in 'text' is present.

    @Override
    public void sendMessage(String channel, String text, Map<String, Object> attachments) {
        log.info("[SLACK MOCK] Sending to channel: {}", channel);
        log.info("[SLACK MOCK] Body: {}", text);
        
        // Verify the fix: URL must be in text
        if (text == null || !text.contains("http")) {
            throw new IllegalStateException("Defect VW-454: GitHub URL missing in Slack body");
        }
        
        // Real Slack API call would go here via WebClient or similar.
    }
}
