package com.example.domain.slack;

import com.example.domain.slack.SlackMessage;
import com.example.ports.SlackNotifier;
import org.springframework.stereotype.Service;

/**
 * Domain Service for Slack Notifications.
 * Acts as an intermediary to handle Slack logic within the domain context,
 * though it delegates the actual sending to the SlackNotifier port.
 */
@Service
public class SlackNotificationService implements SlackNotifier {

    private final SlackNotifier slackNotifier; // Injected port (e.g., HttpSlackAdapter)

    public SlackNotificationService(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void send(SlackMessage message) {
        // Logic to format or validate the message could go here.
        // For now, we delegate directly to the adapter/port.
        slackNotifier.send(message);
    }
}
