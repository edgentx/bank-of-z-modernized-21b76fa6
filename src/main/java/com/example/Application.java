package com.example;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.domain.validation.ReportDefectWorkflow;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Port definitions
    @Bean
    public GitHubPort gitHubPort(RestClient.Builder builder) {
        return new GitHubAdapter(builder);
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(RestClient.Builder builder) {
        String webhookUrl = System.getenv().getOrDefault("SLACK_WEBHOOK_URL", "https://hooks.slack.com/mock");
        return new SlackAdapter(builder, webhookUrl);
    }

    // Workflow Bean
    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        return new ReportDefectWorkflow(gitHubPort, slackPort);
    }
}
