package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.validation.DefectWorkflowService;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort(RestClient restClient) {
        return new GitHubIssueAdapter(
            RestClient.builder(),
            "mock-owner",
            "mock-repo"
        );
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public DefectWorkflowService defectWorkflowService(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
        return new DefectWorkflowService(githubPort, slackPort);
    }
}
