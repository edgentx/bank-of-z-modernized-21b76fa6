package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real adapter for sending notifications to Slack.
 * In a production environment, this would use the Slack WebAPI client.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void sendNotification(String message) {
        // Real-world implementation would use WebClient or Slack API Client here
        // e.g., slackClient.methods().chatPostMessage(req -> req.channel("#vforce360-issues").text(message));
        log.info("[Slack Outbound] Sending message to #vforce360-issues: {}", message);
    }
}
