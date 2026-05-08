package com.example.adapters;

import com.example.ports.GithubPort;
import com.example.vforce.shared.ReportDefectCommand;
import com.squareup.okhttp3.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class GithubAdapter implements GithubPort {

    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.github.com/repos/example/repo/issues";

    @Override
    public String createIssue(ReportDefectCommand command) {
        String json = "{\"title\":\"" + command.title() + "\",\"body\":\"" + command.body() + "\"}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return "https://github.com/example/repo/issues/1" + command.title();
            }
            throw new RuntimeException("Failed to create issue");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}