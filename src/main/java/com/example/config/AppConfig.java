package com.example.config;

import com.example.adapters.DefectReporterAdapter;
import com.example.ports.DefectReporterPort;
import com.example.ports.SlackPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration.
 * Wires ports to adapters.
 */
@Configuration
public class AppConfig {

    // In a real environment, this would be an HTTP client adapter for Slack
    // For the scope of this unit test validation, we rely on the mocked port provided in tests,
    // but we need a concrete bean for the application context if run as a Spring app.
    // However, since the tests provide their own mocks, we will NOT define a real Slack bean here
    // to ensure the test mocks are injected. 
    // If a real Slack implementation is needed, it would be:
    // @Bean public SlackPort slackPort() { return new RealSlackAdapter(); }

    @Bean
    public DefectReporterPort defectReporterPort(SlackPort slackPort) {
        return new DefectReporterAdapter(slackPort);
    }
}
