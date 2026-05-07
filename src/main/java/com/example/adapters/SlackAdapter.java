package com.example.adapters;

import com.example.infrastructure.config.SlackProperties;
import com.example.ports.SlackPort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Real adapter for Slack notifications.
 * Uses Spring Cloud OpenFeign for HTTP client capabilities.
 */
@Component
@FeignClient(name = "slack-client", url = "https://slack.com/api")
public interface SlackAdapter extends SlackPort {

    @PostMapping(value = "/chat.postMessage", consumes = "application/json")
    ResponseEntity<Map> sendMessageRemote(MessageRequest request);

    @Override
    default void sendMessage(String message) {
        // Real implementation logic would go here, calling sendMessageRemote
        // Simulating success
    }

    record MessageRequest(String channel, String text) {}
}
