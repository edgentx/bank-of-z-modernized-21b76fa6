package com.example.steps;

import com.example.domain.shared.Command;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import com.example.application.ReportDefectHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

/**
 * TDD Red Phase Tests for Story S-FB-1.
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (URL missing).
 * 2. Regression test added covering this scenario.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SFB1Steps.TestConfiguration.class)
public class SFB1Steps {

    @Autowired
    private MockSlackNotificationPort mockSlackPort;

    @Autowired
    private ReportDefectHandler handler;

    /**
     * Scenario: Trigger _report_defect and verify URL presence.
     * Given a defect is reported with a GitHub URL
     * When the handler processes the report
     * Then the Slack body must strictly include the GitHub URL
     */
    @Test
    void test_slackBodyContainsGitHubUrl() {
        // Arrange
        String expectedUrl = "https://github.com/example-org/project/issues/454";
        String defectId = "VW-454";
        
        // Mock the temporal-worker exec trigger logic here directly
        // In a real scenario, this might be a Command object dispatched to the Aggregate
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, "Severity LOW", expectedUrl);

        // Act
        // Using the mock adapter to intercept the call that would go to Slack
        // Since we are in Red Phase, this handler might not even exist yet or return null
        try {
            handler.handle(cmd);
        } catch (Exception e) {
            // Expected in Red Phase if class doesn't exist
        }

        // Assert
        // Verify that the Mock captured a call
        // And that the message body contains the URL
        assertTrue(mockSlackPort.wasCalled(), "Slack notification should have been triggered");
        
        String capturedBody = mockSlackPort.getLastMessageBody();
        
        // This is the core validation for the defect fix
        assertNotNull(capturedBody, "Slack body should not be null");
        assertTrue(
            capturedBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL: " + expectedUrl + "\nActual: " + capturedBody
        );
    }

    // Inner class to simulate the minimal Spring Context setup for the test
    // without relying on the main Application.java which might be invalid currently.
    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {
        // In a real repo, these would be beans. Here we simulate the injection.
        // For the sake of this file generation, we assume the test framework handles
        // the instantiation or we use standard JUnit without Spring context if 
        // Spring context creation is failing due to the POM errors.
    }

    // POJO for the command
    record ReportDefectCommand(String defectId, String severity, String githubUrl) implements Command {}
}
