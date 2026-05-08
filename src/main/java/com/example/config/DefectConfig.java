package com.example.config;

import com.example.adapters.DefectRepositoryImpl;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 */
@Configuration
public class DefectConfig {

    @Bean
    public DefectRepository defectRepository() {
        return new DefectRepositoryImpl();
    }

    @Bean
    public IssueTrackerPort issueTrackerPort() {
        // In a real env, this might be swapped for a prod implementation via properties/profiles
        return new GitHubAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackAdapter();
    }
}
