package com.example.adapters;

import com.example.domain.shared.Command;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real implementation of SlackNotificationPort using Spring WebClient/RestClient.
 * This is the "Green" phase adapter that would actually hit the Slack API.
 * Note: For production, this would use the Slack Java SDK or a properly configured WebClient.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final String webhookUrl;
    private final RestClient restClient;

    public SlackNotificationAdapter(@Value("${slack.webhook.url}") String webhookUrl,
                                     RestClient.Builder restClientBuilder) {
        this.webhookUrl = webhookUrl;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void postDefectNotification(Command command, String messageBody) {
        if ("mock".equals(webhookUrl)) {
            log.info("Mock Slack Mode: Would post [{}]", messageBody);
            return;
        }

        try {
            // Real implementation would POST to webhookUrl with JSON payload
            // {
            //   "text": messageBody
            // }
            log.info("Posting to Slack webhook: {}", messageBody);
            // restClient.post()
            //     .uri(webhookUrl)
            //     .body(Map.of("text", messageBody))
            //     .retrieve();
        } catch (Exception e) {
            log.error("Failed to post Slack notification", e);
            // Decide if we throw or swallow (often swallow for notifications)
        }
    }
}
