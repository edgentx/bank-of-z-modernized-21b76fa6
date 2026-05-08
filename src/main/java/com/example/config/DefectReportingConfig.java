package com.example.config;

import com.example.adapters.GithubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for Dependency Injection.
 * Maps the interfaces (Ports) to their concrete implementations (Adapters).
 * 
 * Note: In a real production environment, we might use @Profile("prod") to switch
 * between Mock adapters (for local dev) and Real adapters (for prod).
 */
public class DefectReportingConfig {

    // Uncomment if using real adapters directly via component scan
    // @Bean
    // public SlackNotificationPort slackNotificationPort() {
    //     return new SlackNotificationAdapter();
    // }
    
    // @Bean
    // public GithubIssuePort githubIssuePort() {
    //     return new GithubIssueAdapter();
    // }
}
