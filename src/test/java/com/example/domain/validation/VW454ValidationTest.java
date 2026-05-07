package com.example.domain.validation;

import com.example.Application;
import com.example.domain.shared.Command;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD Red Phase Test for S-FB-1: Validating VW-454.
 *
 * We want to ensure that when a defect is reported via the Temporal workflow,
 * the resulting Slack message body contains the GitHub issue URL.
 */
@SpringBootTest(classes = {Application.class, VW454ValidationTest.TestConfig.class})
public class VW454ValidationTest {

    @Autowired(required = false) // We don't want the test to fail on autowiring if bean is missing yet, but usually we expect the mock
    private SlackNotificationPort slackNotificationPort;

    // We use a static cast or a specific bean name to access our mock if the interface is injected
    // For simplicity in this structure, we assume we can retrieve the mock or the container supplies it.
    // Ideally, we inject the Mock directly.

    @Test
    @ContextConfiguration(classes = Application.class)
    public void contextLoads() {
        // Basic sanity check that Spring context initializes
    }

    /**
     * Acceptance Criteria: Regression test added to e2e/regression/ covering this scenario.
     *
     * This test verifies that the defect reporting workflow produces a valid Slack body
     * containing the GitHub URL.
     */
    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Given
        String expectedUrl = "https://github.com/example-org/vforce360/issues/454";
        // We would trigger a Command here if the workflow entry point is a Command
        // Command cmd = new ReportDefectCmd(...);
        
        // When
        // Assuming the execution happens via Temporal or a synchronous handler for testing
        // handler.handle(cmd);

        // Then
        // This assertion will fail in Red Phase because the implementation doesn't exist
        // or the body is currently empty/missing the link.
        
        if (slackNotificationPort instanceof MockSlackNotificationPort mock) {
            assertThat(mock.getCalls())
                .as("Slack notification should have been triggered")
                .isNotEmpty();

            String body = mock.getCalls().get(0).messageBody;
            assertThat(body)
                .as("Slack body must contain the GitHub issue URL")
                .contains(expectedUrl);
        } else {
            // Fallback if wiring is being set up
            throw new IllegalStateException("MockSlackNotificationPort was not injected. Check configuration.");
        }
    }

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }
    }
}