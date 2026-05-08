package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * VForce360 Validation Service Application.
 * Configures the beans for the Validation Aggregate to use real adapters.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackPort slackPort(
            @Value("${slack.token}") String slackToken,
            @Value("${slack.channel.id}") String slackChannelId) {
        return new SlackAdapter(slackToken, slackChannelId);
    }

    @Bean
    public GitHubPort githubPort(
            @Value("${github.api.url}") String githubApiUrl,
            @Value("${github.auth.token}") String githubAuthToken) {
        return new GitHubAdapter(githubApiUrl, githubAuthToken);
    }
}
