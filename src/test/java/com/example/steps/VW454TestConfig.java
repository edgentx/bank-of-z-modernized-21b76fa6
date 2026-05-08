package com.example.steps;

import com.example.ports.SlackNotifier;
import com.example.ports.TemporalWorkflowStarter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;

/**
 * Spring Configuration for VW-454 tests.
 * This binds the Ports to Mock implementations.
 */
@TestConfiguration
public class VW454TestConfig {

    @Bean
    public SlackNotifier slackNotifier() {
        return new MockSlackNotifier();
    }

    @Bean
    public TemporalWorkflowStarter temporalWorkflowStarter(SlackNotifier slackNotifier) {
        // We inject the mock slack notifier into the starter to ensure interaction
        return new MockTemporalWorkflowStarter(slackNotifier);
    }
}
