package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class SlackNotificationService implements SlackNotificationPort {

    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public SlackNotificationService(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public void sendMessage(String channel, String message, Set<String> mentions) {
        // Stub implementation to satisfy compilation and basic contract
        // Real implementation would build the Slack JSON payload and execute the request.
        // String jsonPayload = "{\"channel\": \"" + channel + "\", \"text\": \"" + message + "\"}";
        // RequestBody body = RequestBody.create(jsonPayload, JSON);
        // Request request = new Request.Builder()
        //         .url("https://slack.com/api/chat.postMessage")
        //         .post(body)
        //         .addHeader("Authorization", "Bearer xoxb-...")
        //         .build();
        // try (Response response = client.newCall(request).execute()) {
        //     if (!response.isSuccessful()) throw new RuntimeException("Failed to send Slack message");
        // } catch (IOException e) {
        //     throw new RuntimeException("IO Error sending Slack message", e);
        // }
    }
}