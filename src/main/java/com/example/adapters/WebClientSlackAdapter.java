package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Real implementation of SlackNotificationPort using Spring WebClient.
 * Posts messages to the Slack Web API.
 */
@Component
public class WebClientSlackAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(WebClientSlackAdapter.class);
    private final WebClient webClient;

    public WebClientSlackAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${slack.webhook.url:}") String webhookUrl
    ) {
        // If the URL is not configured, we might default to a no-op or throw, 
        // but here we build the client targeting the configured URL.
        this.webClient = webClientBuilder
                .baseUrl(webhookUrl)
                .build();
    }

    @Override
    public boolean postMessage(String channel, String body) {
        try {
            // Construct the payload for the Slack Webhook or API
            // Standard webhook payload structure: { "text": "...", "channel": "..." }
            // Note: Incoming Webhooks ignore 'channel' in JSON if pre-configured, but ChatPostMessage uses it.
            // We'll assume a generic payload structure.
            Map<String, String> payload = Map.of(
                "channel", channel,
                "text", body,
                "username", "Defect Bot"
            );

            Boolean success = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Boolean.class) // Assuming API returns true/false or we just catch errors
                    .onErrorReturn(false)
                    .block(Duration.ofSeconds(5)); // Blocking is acceptable for this synchronous interface

            return Boolean.TRUE.equals(success);

        } catch (Exception e) {
            log.error("Slack notification failed", e);
            return false;
        }
    }
}
