package com.example.domain.vforce.adapter;

import com.example.domain.vforce.model.SlackMessage;
import com.example.domain.vforce.port.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack integration.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void notify(SlackMessage message) {
        // Implementation would involve HTTP call to Slack Webhook
        throw new UnsupportedOperationException("Real Slack API call not implemented in this snippet");
    }
}
