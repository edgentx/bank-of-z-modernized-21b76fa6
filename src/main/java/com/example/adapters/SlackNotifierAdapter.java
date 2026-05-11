package com.example.adapters;

import com.example.ports.SlackNotifier;
import com.squareup.okhttp3.MediaType;
import com.squareup.okhttp3.OkHttpClient;
import com.squareup.okhttp3.Request;
import com.squareup.okhttp3.RequestBody;
import com.squareup.okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class SlackNotifierAdapter implements SlackNotifier {

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Override
    public void send(String messageBody) throws IOException {
        RequestBody body = RequestBody.create(messageBody, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
