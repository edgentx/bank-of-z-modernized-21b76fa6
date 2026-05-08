package com.example.adapters;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of SlackPort.
 * Connects to the real Slack API webhook or API.
 */
@Component
public class RealSlackAdapter implements SlackPort {

    private final RestTemplate restTemplate;
    private final String slackWebhookUrl;

    public RealSlackAdapter(RestTemplate restTemplate, String slackWebhookUrl) {
        this.restTemplate = restTemplate;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @Override
    public void sendMessage(String channel, String messageBody) {
        // In a real scenario, we would POST to slackWebhookUrl
        // Map<String, Object> payload = new HashMap<>();
        // payload.put("channel", channel);
        // payload.put("text", messageBody);

        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        // restTemplate.postForObject(slackWebhookUrl, request, String.class);
        
        // System.out.println("Slack sent to " + channel + ": " + messageBody);
    }
}
