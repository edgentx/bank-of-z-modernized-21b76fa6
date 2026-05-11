package com.example.config;

import com.example.adapters.DefectRepositoryAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.defect.repository.DefectRepositoryPort;
import com.example.domain.defect.service.SlackNotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectConfig {

    @Bean
    public DefectRepositoryPort defectRepositoryPort() {
        return new DefectRepositoryAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
