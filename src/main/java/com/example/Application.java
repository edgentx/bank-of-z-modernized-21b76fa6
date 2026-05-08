package com.example;

import com.example.adapters.impl.GitHubAdapterImpl;
import com.example.adapters.impl.SlackAdapterImpl;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application for Bank of Z Modernization.
 * Configures Ports and Adapters for external integrations (Temporal, Slack, GitHub).
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }

    /**
     * Real implementation of GitHubPort.
     * Injected into workflows (e.g., _report_defect).
     */
    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapterImpl();
    }

    /**
     * Real implementation of SlackPort.
     * Injected into workflows to post results.
     */
    @Bean
    public SlackPort slackPort() {
        return new SlackAdapterImpl();
    }
}
