package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(name = "app.slack.enabled", havingValue = "true", matchIfMissing = false)
public class DefaultSlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(DefaultSlackAdapter.class);
    private final MethodsClient methodsClient;
    private final String token;

    public DefaultSlackAdapter(MethodsClient methodsClient, @Value("${app.slack.token}") String token) {
        this.methodsClient = methodsClient;
        this.token = token;
    }

    @Override
    public boolean postMessage(String channelId, String text) {
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text(text)
                .build();

            ChatPostMessageResponse response = methodsClient.chatPostMessage(request);
            return response.isOk();
        } catch (IOException | SlackApiException e) {
            log.error("Failed to post message to Slack channel {}: {}", channelId, e.getMessage());
            return false;
        }
    }
}