package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Real implementation of SlackPort using Slack API Client.
 * This adapter is responsible for the actual HTTP communication with Slack.
 */
public class SlackAdapter implements SlackPort {

    private static final Logger logger = LoggerFactory.getLogger(SlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String token; // In production, inject or manage via secure config

    public SlackAdapter(MethodsClient methodsClient, String token) {
        this.methodsClient = methodsClient;
        this.token = token;
    }

    @Override
    public void sendMessage(String channelId, String message) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text(message)
                    .token(token) // Token usually required in the request object for some client versions, or set globally
                    .build();
            
            // Execute the API call
            methodsClient.chatPostMessage(request);
            
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to send Slack message to channel {}", channelId, e);
            throw new RuntimeException("Failed to send Slack message", e);
        }
    }
}
