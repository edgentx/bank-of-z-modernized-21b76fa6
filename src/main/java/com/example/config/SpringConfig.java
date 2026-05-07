package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpringConfig {

    @Value("${github.repo.owner}")
    private String repoOwner;

    @Value("${github.repo.name}")
    private String repoName;

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public GitHubIssueAdapter gitHubIssueAdapter(OkHttpClient client, ObjectMapper mapper) {
        return new GitHubIssueAdapter(client, mapper, repoOwner, repoName, githubToken);
    }

    @Bean
    public SlackNotificationAdapter slackNotificationAdapter(OkHttpClient client, ObjectMapper mapper, 
                                                             @Value("${slack.webhook.url}") String webhookUrl) {
        return new SlackNotificationAdapter(client, mapper, webhookUrl);
    }
}
