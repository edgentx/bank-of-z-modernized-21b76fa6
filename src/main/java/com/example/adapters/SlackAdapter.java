package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of the Slack Port using the Slack API Client.
 * Configured via Spring Boot properties.
 */
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String channelId;

    public SlackAdapter(String authToken, String channelId) {
        this.channelId = channelId;
        this.methodsClient = Slack.getInstance().methods(authToken);
    }

    @Override
    public boolean postMessage(String text) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text(text)
                .build();

            ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
            
            if (response.isOk()) {
                log.info("Successfully posted message to Slack channel {}", channelId);
                return true;
            } else {
                log.error("Failed to post message to Slack: {} - {}", response.getError(), response.getResponseMetadata());
                return false;
            }
        } catch (Exception e) {
            log.error("Exception occurred while posting to Slack", e);
            return false;
        }
    }
}
