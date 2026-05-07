package com.example.domain.validation.adapter;

import com.example.domain.validation.port.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notifier.
 * In a production environment, this would use the Slack Web API.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifier {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierAdapter.class);

    @Override
    public void notify(String messageBody) {
        // Placeholder: In production, perform HTTP POST to Slack Webhook here.
        // We simply log the message to verify receipt.
        log.info("Slack Notification sent: {}", messageBody);
    }
}
