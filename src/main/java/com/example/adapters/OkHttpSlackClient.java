package com.example.adapters;

import com.example.ports.SlackWebhookPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Implementation of SlackWebhookPort using OkHttp.
 */
@Component
public class OkHttpSlackClient implements SlackWebhookPort {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    // In production, this URL would be externalized to configuration
    private static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/FAKE/WEBHOOK/URL";

    @Override
    public void send(String body) {
        // Real implementation would POST 'body' to SLACK_WEBHOOK_URL
        // MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        // Request request = new Request.Builder()
        //     .url(SLACK_WEBHOOK_URL)
        //     .post(RequestBody.create(body, mediaType))
        //     .build();
        
        try {
            // Response response = client.newCall(request).execute();
            // if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            
            // Stub: Do nothing for the build fix, logging would go here.
            System.out.println("[Slack] Sending payload: " + body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Slack notification", e);
        }
    }
}
