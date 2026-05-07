package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.workflows.DefectReportWorkflow;
import com.example.workflows.DefectReportActivities;
import com.example.workflows.DefectReportActivitiesImpl;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public DefectReportActivities defectReportActivities(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        return new DefectReportActivitiesImpl(gitHubPort, slackPort);
    }
}
