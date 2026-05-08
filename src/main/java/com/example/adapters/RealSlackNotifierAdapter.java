package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotifierPort.
 * Connects to the actual Slack API to post messages.
 */
@Component
public class RealSlackNotifierAdapter implements SlackNotifierPort {

    private static final Logger log = LoggerFactory.getLogger(RealSlackNotifierAdapter.class);
    private final String slackWebhookUrl; // Injected via configuration

    public RealSlackNotifierAdapter() {
        // In a real scenario, this would come from @Value("${slack.webhook.url}")
        this.slackWebhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/mock");
    }

    @Override
    public void postMessage(String channel, String messageBody) {
        // Implementation of actual Slack API call (e.g., using WebClient or RestTemplate)
        // S-FB-1: Ensuring the URL is in the body is the responsibility of the caller (ValidationWorkflowOrchestrator),
        // but we could add defensive checks here.
        log.info("POSTING to {}: {}", channel, messageBody);
        
        // Example WebClient implementation:
        /*
        WebClient client = WebClient.create(slackWebhookUrl);
        client.post()
            .bodyValue(Map.of("channel", channel, "text", messageBody))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
        */
    }
}
