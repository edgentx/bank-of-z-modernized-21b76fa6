package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.service.ReportDefectService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackNotificationAdapter adapter) {
        return adapter;
    }

    @Bean
    public GitHubPort gitHubPort(GitHubAdapter adapter) {
        return adapter;
    }

    @Bean
    public ReportDefectService reportDefectService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        return new ReportDefectService(gitHubPort, slackNotificationPort);
    }
}
