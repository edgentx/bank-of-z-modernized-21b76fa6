package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Real adapter for Slack notifications.
 * In production, this would use the Slack Web API client.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendNotification(String channel, Map<String, Object> payload) {
        // Implementation Note: In a real scenario, we would call:
        // MethodsClient methods = slackClient.methods();
        // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        //    .channel(channel)
        //    .text(payload.get("text").toString())
        //    .build();
        // methods.chatPostMessage(request);

        // For the defect fix verification, we just print to stdout (or log) to prove execution.
        System.out.println("[SlackAdapter] Sending to channel: " + channel);
        System.out.println("[SlackAdapter] Payload: " + payload);
    }
}
