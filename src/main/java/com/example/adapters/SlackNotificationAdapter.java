package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notification Port.
 * In a production environment, this would make an HTTP call to the Slack Web API.
 * For the scope of S-FB-1 (Logic Fix), this acts as the stub placeholder,
 * logging the output to verify the build pipeline.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public boolean postMessage(String body) {
        // Production Note: Replace this with WebClient/RestTemplate call to Slack API.
        // Example: webClient.post().uri(SLACK_WEBHOOK_URL).bodyValue(body).retrieve();
        log.info("[Slack Adapter] Posting message: {}", body);
        return true;
    }
}
