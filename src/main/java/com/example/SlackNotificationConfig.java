package com.example;

import com.example.adapters.RealGithubAdapter;
import com.example.adapters.RealSlackAdapter;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Notification infrastructure.
 * Instantiates real adapters if no mocks are provided by tests.
 */
@Configuration
public class SlackNotificationConfig {

    @Bean
    @ConditionalOnMissingBean(GithubPort.class)
    public GithubPort githubPort() {
        return new RealGithubAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    public SlackNotificationPort slackNotificationPort() {
        return new RealSlackAdapter();
    }
}
