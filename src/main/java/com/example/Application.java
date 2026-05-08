package com.example;

import com.example.adapters.SlackAdapter;
import com.example.ports.SlackPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackPort slackPort() {
        // In a real production environment, this might be configured with properties.
        // For the defect fix validation, we provide the concrete implementation.
        return new SlackAdapter();
    }
}
