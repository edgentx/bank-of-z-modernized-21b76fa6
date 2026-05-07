package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Ports and Adapters.
 * In a real environment, active profiles would dictate whether real or mock adapters are used.
 * Here we define the real adapters as primary beans; tests are expected to override these via
 * @Primary or test-specific configurations in src/test.
 */
@Configuration
public class SpringConfig {

    @Bean
    @ConditionalOnMissingBean(GitHubIssuePort.class)
    public GitHubIssuePort gitHubIssuePort() {
        return new GitHubIssueAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
