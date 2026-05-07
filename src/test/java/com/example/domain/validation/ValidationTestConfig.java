package com.example.domain.validation;

import com.example.domain.validation.ports.DefectReporter;
import com.example.domain.validation.ports.SlackPublisher;
import com.example.mocks.MockSlackPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for Validation domain.
 * Binds mock adapters for external dependencies.
 */
@Configuration
public class ValidationTestConfig {

    @Bean
    @Primary
    public SlackPublisher mockSlackPublisher() {
        return new MockSlackPublisher();
    }

    @Bean
    public DefectReporter defectReporter(SlackPublisher slackPublisher) {
        // In a real scenario, this would be a Service injected with Temporal/GitHub clients.
        // For the Red Phase, we assume the bean exists or mock it if creating a stub implementation.
        // Here we return a simple stub that mimics the behavior we want to test.
        return new DefectReporter() {
            @Override
            public void reportDefect(String title, String description) {
                // Simulated behavior: Create GitHub URL
                String issueId = title.split("—")[0].trim().replace("VW-", "");
                String url = "https://github.com/egdcrypto/bank-of-z-modernized/issues/" + issueId;
                
                // Simulated behavior: Post to Slack
                slackPublisher.publishMessage("#vforce360-issues", Map.of("text", "Defect reported: " + url));
            }
        };
    }
}