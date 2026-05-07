package com.example.adapters;

import com.example.domain.validation.model.DefectReportedEvent;
import org.springframework.stereotype.Component;

/**
 * Adapter responsible for sending notifications to Slack.
 */
@Component
public class SlackNotificationAdapter {

    /**
     * Publishes the defect event to the #vforce360-issues channel.
     * Relies on the event's slackBody() method to format the message correctly.
     */
    public void publishDefect(DefectReportedEvent event) {
        // Implementation would post to Slack Webhook
        String body = event.slackBody();
        if (!body.contains("http")) {
            throw new IllegalStateException("Slack body must contain a valid URL");
        }
        System.out.println("SLACK SENT: " + body);
    }
}
