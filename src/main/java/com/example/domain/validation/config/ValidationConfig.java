package com.example.domain.validation.config;

import com.example.domain.validation.ports.GitHubClient;
import com.example.domain.validation.ports.SlackPublisher;
import com.example.domain.validation.service.DefectReportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    @ConditionalOnMissingBean
    public DefectReportService defectReportService(GitHubClient gitHubClient, SlackPublisher slackPublisher) {
        return new DefectReportService(gitHubClient, slackPublisher);
    }
}