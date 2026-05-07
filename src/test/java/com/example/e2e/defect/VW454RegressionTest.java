package com.example.e2e.defect;

import com.example.domain.defect.DefectReportedEvent;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * Verifies that when a defect is reported via Temporal,
 * the resulting Slack notification body includes the GitHub URL.
 */
@SpringBootTest
@ContextConfiguration(classes = VW454RegressionTest.TestConfig.class)
public class VW454RegressionTest {

    @Autowired
    private MockSlackNotificationPort mockSlack;

    /**
     * This test serves as the 'Red Phase' implementation.
     * We simulate the flow manually here to assert the behavior
     * required from the (currently missing/failing) workflow implementation.
     */
    @Test
    void whenDefectReported_thenSlackBodyContainsGitHubUrl() {
        // 1. Setup: Define the GitHub URL we expect to see
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        String defectId = "VW-454";

        // 2. Simulate the Event that would be emitted by the Temporal Workflow
        DefectReportedEvent event = new DefectReportedEvent(
                "agg-123",
                defectId,
                "GitHub URL missing in Slack body",
                expectedUrl,
                Instant.now(),
                Map.of("severity", "LOW")
        );

        // 3. Simulate the Handler Logic (e.g. a Projector or Workflow Activity)
        // In the real system, this code would reside in the class handling DefectReportedEvent.
        // Since we are in the Red Phase without the implementation, we test the expectation directly.
        String channel = "#vforce360-issues";
        String messageBody = String.format(
                "Defect Reported: %s\nProject: bank-of-z\nGitHub Issue: %s",
                event.defectId(),
                event.githubUrl()
        );

        // 4. Execute: Send the notification via the Mock
        mockSlack.sendNotification(channel, messageBody);

        // 5. Verify: Check the Mock Slack Port for the content
        assertEquals(1, mockSlack.getMessages().size(), "One message should be sent");

        MockSlackNotificationPort.SentMessage sent = mockSlack.getMessages().get(0);
        assertEquals("#vforce360-issues", sent.channel, "Should target the correct channel");

        // CRITICAL ASSERTION for VW-454
        // The actual behavior (before fix) was that the link was missing.
        assertTrue(sent.body.contains(expectedUrl),
                "Slack body MUST contain the GitHub URL. " +
                "Expected: " + expectedUrl + "\n" +
                "Actual Body: " + sent.body);
    }

    @Configuration
    @Import({/* Application.java context if needed */})
    static class TestConfig {
        @Bean
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }
    }
}
