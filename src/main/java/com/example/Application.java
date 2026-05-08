package com.example;

import com.example.adapters.RealSlackAdapter;
import com.example.adapters.RealGitHubAdapter;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Bank of Z Modernization Application Entry Point.
 * Configures real adapters for external ports.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackPort slackPort() {
        // In a real environment, this would be configured with actual tokens/URLs via properties
        return new RealSlackAdapter();
    }

    @Bean
    public GitHubPort gitHubPort() {
        // In a real environment, this would be configured with actual tokens/URLs via properties
        return new RealGitHubAdapter();
    }

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ReportDefectWorkflowImpl(gitHubPort, slackPort);
    }
}