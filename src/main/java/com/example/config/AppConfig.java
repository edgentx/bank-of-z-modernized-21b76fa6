package com.example.config;

import com.example.adapters.GithubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GitHubPort gitHubPort(RestTemplate restTemplate,
                                 @Value("${github.api.url}") String apiUrl,
                                 @Value("${github.auth.token}") String authToken) {
        return new GithubIssueAdapter(restTemplate, apiUrl, authToken);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(RestTemplate restTemplate,
                                                        @Value("${slack.webhook.url}") String webhookUrl) {
        return new SlackNotificationAdapter(restTemplate, webhookUrl);
    }
}
