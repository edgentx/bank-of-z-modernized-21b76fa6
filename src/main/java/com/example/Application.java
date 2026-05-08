package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.TemporalWorkerAdapter;

/**
 * Main Spring Boot Application entry point.
 * Defines the Composition Root for the application, wiring Ports to Adapters.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // In a real environment, this would return the real Slack adapter implementation.
        // For the context of this E2E regression fix, we might return a real implementation
        // or rely on the test configuration overriding this bean.
        return new SlackNotificationPort() {
            @Override
            public void send(String body) {
                // Real implementation would go here (e.g., WebClient call to Slack API)
                System.out.println("[Slack] Sending notification: " + body);
            }
        };
    }

    @Bean
    public TemporalWorkerAdapter temporalWorker(SlackNotificationPort slackNotificationPort) {
        // Wire the worker with the necessary dependencies.
        return new TemporalWorkerAdapter(slackNotificationPort);
    }
}
