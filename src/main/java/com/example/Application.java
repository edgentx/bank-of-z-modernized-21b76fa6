package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Main Spring Boot Application class.
 * Scans components, adapters, and configuration.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }

    /**
     * Configuration for the GitHub implementation.
     * In a real environment, this would use @Value to inject API tokens and endpoints.
     */
    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter();
    }

    /**
     * Configuration for the Slack implementation.
     * In a real environment, this would use @Value to inject Webhook URLs or OAuth tokens.
     */
    @Bean
    public SlackPort slackPort() {
        return new SlackAdapter();
    }
}
