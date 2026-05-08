package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.service.ValidationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GitHubPort gitHubPort() {
        // In production, use the real adapter.
        return new GitHubAdapter();
    }

    @Bean
    public SlackPort slackPort() {
        // In production, use the real adapter.
        return new SlackAdapter();
    }

    @Bean
    public ValidationService validationService(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ValidationService(gitHubPort, slackPort);
    }
}
