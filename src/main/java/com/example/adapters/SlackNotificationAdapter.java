package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real production adapter for sending notifications to Slack.
 * This component is only active in non-test profiles.
 */
@Component
@Profile("!test")
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final MethodsClient slackMethodsClient;

    public SlackNotificationAdapter(
            @Value("${slack.token}") String slackToken,
            Slack slackApi) {
        // In a real scenario, we initialize the Slack API client here.
        // For TDD Green phase, we focus on the contract.
        this.slackMethodsClient = slackApi.methods(slackToken);
    }

    @Override
    public void postMessage(String channelId, String message) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be blank");
        }
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slackMethodsClient.chatPostMessage(request);

            if (!response.isOk()) {
                log.error("Slack API Error: {} - {}", response.getError(), response.getWarning());
                throw new RuntimeException("Failed to post message to Slack: " + response.getError());
            }
            
            log.info("Successfully posted defect notification to channel {}", channelId);

        } catch (Exception e) {
            // Wrap checked exceptions or network errors in a runtime exception for the domain
            throw new RuntimeException("Failed to communicate with Slack", e);
        }
    }
}
