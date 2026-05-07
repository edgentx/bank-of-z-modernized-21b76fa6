package com.example;

import com.example.application.DefectReportingService;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configuration for the Slack Notification Port.
     * In a production environment, this would return the real SlackAdapter.
     * For the purposes of this build/test cycle, we can expose the mock or the interface.
     * 
     * Ideally, we use @Profile to switch, but for simplicity we define the bean here.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // Returning the real implementation would go here:
        // return new SlackAdapter();
        
        // For the unit/e2e tests provided, we rely on the mocks initialized in the test context,
        // but a primary bean is often needed for context loading if not using MockBean.
        // However, since the tests instantiate the Mock manually, we can just return a no-op
        // or placeholder if the context loads, or rely on the tests providing their own context.
        // 
        // To ensure the Application.java runs without external dependencies (like Slack WebAPI clients):
        return new SlackNotificationPort() {
            @Override
            public void send(String channel, String message) {
                // No-op for the default context load
                System.out.println("[DEFAULT SLACK PORT] To " + channel + ": " + message);
            }
        };
    }

    @Bean
    public DefectReportingService defectReportingService(SlackNotificationPort port) {
        return new DefectReportingService(port);
    }
}
