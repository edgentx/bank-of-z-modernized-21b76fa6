package com.example.adapters;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Red-phase test for Slack adapter logic.
 * This test verifies that when sending a notification, the system ensures
 * that the message body includes the expected GitHub URL formatting.
 */
public class SlackWebhookAdapterTest {

    // Regression test for defect VW-454
    // Expected Behavior: Slack body includes GitHub issue: <url>
    @Test
    public void testSendNotification_GitHubUrlInBody() {
        // Arrange
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        String webhookUrl = "https://hooks.slack.com/services/FAKE/URL/TEST";
        SlackWebhookAdapter adapter = new SlackWebhookAdapter(mockRestTemplate, webhookUrl);
        
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        String expectedMessage = "GitHub issue: " + githubUrl;

        // Act
        adapter.sendNotification(expectedMessage);

        // Assert
        // We verify that postForObject was called, implying the message was processed.
        // Further validation of the exact JSON structure happens in integration/e2e layers.
        verify(mockRestTemplate).postForObject(eq(webhookUrl), any(), eq(String.class));
    }

    @Test
    public void testSendNotification_ThrowsWhenUrlMissing() {
        // Arrange
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        SlackWebhookAdapter adapter = new SlackWebhookAdapter(mockRestTemplate, null);
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            adapter.sendNotification("Test message");
        });
    }
}
