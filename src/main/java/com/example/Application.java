package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import com.example.adapters.SlackAdapter;
import com.example.adapters.GitHubAdapter;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SlackPort slackPort() {
        return new SlackAdapter();
    }

    @Bean
    public GitHubPort githubPort() {
        return new GitHubAdapter();
    }
}