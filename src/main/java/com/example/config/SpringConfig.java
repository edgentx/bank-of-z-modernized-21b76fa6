package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.application.DefectReportingActivity;
import com.example.domain.verification.service.VerificationService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Configuration
@Import({AdapterConfiguration.class})
public class SpringConfig {

    /**
     * Bean for the Domain Service.
     * Depends on Ports (Interfaces), which are implemented by Adapters.
     */
    @Bean
    public VerificationService verificationService(
            GitHubPort gitHubPort,
            SlackNotificationPort slackNotificationPort) {
        return new VerificationService(gitHubPort, slackNotificationPort);
    }

    // The specific Adapter implementations are auto-detected by component scanning (@Component),
    // so we don't strictly need to define them as beans here unless we need specific configuration.
    // However, to ensure the Port interfaces are injected correctly into the Service:
    
    @Bean
    public GitHubPort gitHubPort(GitHubAdapter gitHubAdapter) {
        return gitHubAdapter;
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(SlackNotificationAdapter slackNotificationAdapter) {
        return slackNotificationAdapter;
    }
}
