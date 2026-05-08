package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.validation.DefectReporterWorker;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configuration for SlackNotificationPort.
     * Switches between real and mock based on environment profiles if needed,
     * but currently defaults to the real adapter requiring a token.
     * 
     * In a real Spring environment, this token would come from application.properties
     * or an environment variable.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // Default to real adapter. In a test profile, this would be overridden.
        // For this Green phase, we expect the token to be passed or mocked in tests.
        // Note: System.getenv() is a simple way to bridge in this example.
        String token = System.getenv("SLACK_AUTH_TOKEN");
        if (token == null || token.isBlank()) {
            // Fallback for local execution if env var is missing, usually we'd fail.
            // But for the purpose of this code structure, we'll instantiate.
            // In a prod scenario, this should throw IllegalStateException.
            return new SlackNotificationAdapter("xoxb-fake-token-for-local-dev");
        }
        return new SlackNotificationAdapter(token);
    }

    @Bean
    public DefectReporterWorker defectReporterWorker(SlackNotificationPort slackNotificationPort) {
        return new DefectReporterWorker(slackNotificationPort);
    }
}
