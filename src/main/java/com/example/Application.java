package com.example;

import com.example.adapters.TemporalWorkerAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.adapters", "com.example.ports"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public TemporalWorkerAdapter temporalWorkerAdapter(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        return new TemporalWorkerAdapter(gitHubPort, slackNotifierPort);
    }
}
