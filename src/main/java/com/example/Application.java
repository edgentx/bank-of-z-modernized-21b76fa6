package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.ports", "com.example.adapters", "com.example.configuration"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackNotificationAdapter adapter) {
        // Returning the real adapter. In tests, this can be overridden by @TestConfiguration.
        return adapter;
    }
}
