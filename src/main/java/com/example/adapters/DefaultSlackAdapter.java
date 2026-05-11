package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.Slack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Default implementation of SlackNotifierPort using the official Slack API client.
 */
public class DefaultSlackAdapter implements SlackNotifierPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String token;

    public DefaultSlackAdapter(String token) {
        this.token = token;
        this.methodsClient = Slack.getInstance().methods(token);
    }

    @Override
    public boolean notify(String channelId, String message) {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text(message)
                .build();

        try {
            ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
            return response.isOk();
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to send Slack notification to channel {}: {}", channelId, e.getMessage(), e);
            return false;
        }
    }
}