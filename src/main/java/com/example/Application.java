package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GitHubPort gitHubPort(GitHubAdapter adapter) {
        return adapter;
    }

    @Bean
    public SlackPort slackPort(SlackAdapter adapter) {
        return adapter;
    }
}
