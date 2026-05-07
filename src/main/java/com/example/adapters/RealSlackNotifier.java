package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotifier.
 * In a production environment, this would use an HTTP client (e.g., WebClient or RestTemplate)
 * to post to the Slack Web API.
 */
@Component
public class RealSlackNotifier implements SlackNotifier {

    private static final Logger logger = LoggerFactory.getLogger(RealSlackNotifier.class);

    @Override
    public void send(String messageBody) {
        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("messageBody cannot be null or empty");
        }

        // Placeholder for actual Slack API logic
        // In a real scenario, we would inject a WebClient here and POST to the webhook URL.
        logger.info("[SLACK] Sending message: {}", messageBody);
        
        // Simulate network latency or processing time
        try {
            Thread.sleep(50); // 50ms simulated delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
