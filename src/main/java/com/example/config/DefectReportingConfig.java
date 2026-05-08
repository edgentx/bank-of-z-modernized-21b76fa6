package com.example.config;

import com.example.activities.DefectReportingActivities;
import com.example.activities.DefectReportingActivitiesImpl;
import com.example.adapters.GitHubRestAdapter;
import com.example.adapters.OkHttpSlackClient;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Wires the ports and adapters together.
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    public SlackPort slackClient() {
        return new OkHttpSlackClient();
    }

    @Bean
    public GitHubPort gitHubAdapter() {
        return new GitHubRestAdapter();
    }

    @Bean
    public DefectReportingActivities defectReportingActivities(SlackPort slackClient, GitHubPort gitHubAdapter) {
        return new DefectReportingActivitiesImpl(slackClient, gitHubAdapter);
    }
}