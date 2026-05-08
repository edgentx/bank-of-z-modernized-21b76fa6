package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.ports.SlackNotificationPort;
import com.example.adapters.SlackNotificationAdapter;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // Returning the real adapter.
        // In a test context, this bean would be overridden by the MockSlackNotificationPort.
        return new SlackNotificationAdapter();
    }
}
