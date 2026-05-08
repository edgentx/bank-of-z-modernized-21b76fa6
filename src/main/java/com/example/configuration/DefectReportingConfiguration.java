package com.example.configuration;

import com.example.adapters.ValidationRepositoryImpl;
import com.example.adapters.WebhookSlackNotificationAdapter;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.shared.SlackMessageValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectReportingConfiguration {

    @Bean
    public DefectRepository defectRepository() {
        return new ValidationRepositoryImpl();
    }

    @Bean
    public SlackMessageValidator slackMessageValidator() {
        return new WebhookSlackNotificationAdapter();
    }
}
