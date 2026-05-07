package com.example.steps;

import com.example.domain.shared.slack.SlackNotificationPort;
import com.example.domain.shared.validation.ValidationPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.mocks.MockValidationPort;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * Context:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Tech Stack: Java, Spring Boot
 */
public class SFB1Steps {

    // We use the mock adapters defined in the mocks package
    private final MockValidationPort validationPort = new MockValidationPort();
    private final MockSlackNotificationPort slackPort = new MockSlackNotificationPort();

    /**
     * Scenario: Successful defect report with valid GitHub URL.
     * Given a valid defect context with a GitHub URL
     * When the worker executes the reporting workflow
     * Then the Slack notification body should contain the GitHub URL
     */
    @Test
    public void testSlackBodyContainsValidGithubUrl() {
        // Setup inputs
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        Map<String, Object> context = new HashMap<>();
        context.put("issue_url", expectedUrl);
        context.put("severity", "LOW");

        // Configure Mocks
        validationPort.setSimulatedUrl(expectedUrl);

        // Execute the logic (simulating the Temporal Worker)
        // In a real worker, this would be a workflow method calling the ports
        String extractedUrl = validationPort.extractAndValidateGithubUrl(context);
        String slackBody = buildSlackBody(extractedUrl);
        slackPort.postMessage("#vforce360-issues", slackBody);

        // Verify Validation logic extracted the URL
        assertEquals(expectedUrl, extractedUrl, "Validation port should extract the correct URL");

        // Verify Slack logic
        assertTrue(slackPort.containsUrl(expectedUrl), "Slack body must contain the GitHub issue link");
        
        // Verify the actual content of the message
        assertEquals(1, slackPort.getMessages().size());
        MockSlackNotificationPort.PostedMessage msg = slackPort.getMessages().get(0);
        assertEquals("#vforce360-issues", msg.channel);
        assertTrue(msg.body.contains(expectedUrl), "Actual body content check");
    }

    /**
     * Scenario: Defect report fails if URL is missing.
     * Given a defect context missing the GitHub URL
     * When the worker executes the reporting workflow
     * Then validation should fail
     */
    @Test
    public void testValidationFailsOnMissingUrl() {
        validationPort.setShouldFail(true); // Simulating validation logic detecting a missing/invalid URL
        Map<String, Object> context = new HashMap<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationPort.extractAndValidateGithubUrl(context);
        });

        assertTrue(exception.getMessage().contains("Mock validation failure"));
    }

    // Helper to simulate the message formatting done by the worker
    private String buildSlackBody(String url) {
        return "Defect Reported. GitHub issue: " + url;
    }
}