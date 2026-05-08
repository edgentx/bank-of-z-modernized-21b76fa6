package com.example;

import com.example.adapters.SlackNotifierAdapter;
import com.example.ports.NotifierPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }

    @Bean
    public NotifierPort notifierPort() {
        // In a real environment, this would be configured with Slack tokens/URLs.
        // For now, we provide the concrete adapter implementation.
        return new SlackNotifierAdapter();
    }
}
