package com.example.config;

import com.example.adapters.DefaultSlackAdapter;
import com.example.ports.SlackPort;
import com.slack.api.Slack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring Configuration for Defect Reporting components.
 * Wires up the Slack Port and Adapters.
 */
@Configuration
public class DefectReportingConfig {

    /**
     * Configures the real Slack Adapter for production or integration tests.
     * This requires a valid Slack Token to be configured in the environment.
     */
    @Bean
    @Profile("!mock")
    public SlackPort slackAdapter(Slack slackInstance, @Value("${slack.token}") String token) {
        return new DefaultSlackAdapter(slackInstance, token);
    }

    /**
     * Configures the default Slack instance used by the adapter.
     */
    @Bean
    public Slack slackInstance() {
        return Slack.getInstance();
    }
}
