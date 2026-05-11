package com.example.config;

import com.example.adapters.DefaultSlackAdapter;
import com.example.adapters.GitHubAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import com.slack.api.Slack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectReportingConfig {

    @Value("${slack.token:}")
    private String slackToken;

    @Bean
    public Slack slack() {
        return Slack.getInstance();
    }

    @Bean
    public SlackNotifierPort slackNotifier(Slack slack) {
        // In a real env, token comes from env/vault
        return new DefaultSlackAdapter(System.getenv().getOrDefault("SLACK_TOKEN", "xoxb-fake-token"));
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter();
    }
}