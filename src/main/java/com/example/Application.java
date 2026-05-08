package com.example;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // We use the Mock implementation by default for this test suite
        // to avoid external dependencies during unit/regression testing.
        // In a real profile (prod), this would be replaced by the real implementation.
        return new MockSlackNotificationPort();
    }
}
