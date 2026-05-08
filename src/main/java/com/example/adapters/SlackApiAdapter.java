package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Production implementation of the SlackNotificationPort.
 * This adapter would interact with the actual Slack Web API.
 * <p>
 * For the purpose of this defect fix, the focus is on the correct
 * formatting of the message body, which is handled in the workflow logic.
 * </p>
 */
@Component
@Profile("!test") // Active unless the 'test' profile is active
public class SlackApiAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackApiAdapter.class);

    @Override
    public void sendMessage(String channel, String messageBody) {
        // In a real scenario, this would use an HTTP client (e.g., WebClient or RestTemplate)
        // to POST to the Slack API webhook.
        log.info("[SLACK ADAPTER] Sending message to channel {}: {}", channel, messageBody);
        
        /*
        Example implementation (commented out to avoid external dependencies in this fix):
        
        WebClient webClient = WebClient.create();
        webClient.post()
            .uri(slackWebhookUrl)
            .bodyValue(Map.of(
                "channel", channel,
                "text", messageBody
            ))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
        */
    }
}
