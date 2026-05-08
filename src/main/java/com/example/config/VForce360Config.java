package com.example.config;

import com.example.adapters.GithubAdapter;
import com.example.adapters.SlackNotificationService;
import com.example.adapters.RealGitHubIssueAdapter;
import com.example.domain.vforce360.service.VForce360Service;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VForce360Config {

    @Bean
    public GithubPort githubPort(
            @Value("${github.api.url}") String apiUrl,
            @Value("${github.api.token}") String token) {
        return new GithubAdapter(apiUrl, token);
    }

    @Bean
    public SlackNotifier slackNotifier(
            @Value("${slack.webhook.url}") String webhookUrl) {
        return new SlackNotificationService(webhookUrl);
    }

    @Bean
    public VForce360Service vForce360Service(GithubPort githubPort, SlackNotifier slackNotifier) {
        return new VForce360Service(githubPort, slackNotifier);
    }

    @Bean
    public RealGitHubIssueAdapter realGitHubIssueAdapter(VForce360Service vForce360Service) {
        // The Adapter bean acts as the temporal workflow entry point
        return new RealGitHubIssueAdapter(vForce360Service, null, null); // Ports are injected into VForce360Service directly
    }
}