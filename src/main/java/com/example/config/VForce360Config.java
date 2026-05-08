package com.example.config;

import com.example.adapters.DefaultSlackAdapter;
import com.example.adapters.GitHubAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class VForce360Config {

    /**
     * Real Slack Adapter Bean.
     * Active when 'slack.webhook.url' is defined.
     */
    @Bean
    @ConditionalOnProperty(name = "slack.webhook.url")
    public SlackNotificationPort slackNotificationPort(DefaultSlackAdapter adapter) {
        return adapter;
    }

    /**
     * Real GitHub Adapter Bean.
     * Active when 'github.auth.token' is defined.
     */
    @Bean
    @ConditionalOnProperty(name = "github.auth.token")
    public GitHubPort gitHubPort(GitHubAdapter adapter) {
        return adapter;
    }
}
