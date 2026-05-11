package com.example;

import com.example.adapters.SlackNotificationAdapter;
import com.example.adapters.GitHubIssueAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.workflow.ReportDefectWorkflow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new GitHubIssueAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        return new ReportDefectWorkflow(gitHubIssuePort, slackNotificationPort);
    }
}
