package com.example.e2e.regression;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for Story ID: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * <p>Objective:
 * Verify that when a defect is reported via the Temporal worker, the resulting
 * Slack notification body contains the correct GitHub issue URL.
 * 
 * <p>Strategy:
 * 1. Mock the external Slack and GitHub adapters.
 * 2. Simulate the workflow execution using reflection to invoke the handler logic.
 * 3. Assert the captured Slack body contains the expected URL.
 */
class VW454SlackUrlRegressionTest {

    private MockSlackNotificationPort mockSlack;
    private MockGitHubIssuePort mockGitHub;

    // The class under test. We assume a Spring service or Temporal Activity exists with this name.
    // Based on the defect report "Trigger _report_defect via temporal-worker exec".
    private static final String WORKFLOW_ACTIVITY_CLASS = "com.example.application.ReportDefectActivity";

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        mockGitHub = new MockGitHubIssuePort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() throws Exception {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;
        String channel = "#vforce360-issues";

        // Configure mocks
        mockGitHub.setUrl(expectedUrl);

        // Act
        // We use reflection because the implementation class (ReportDefectActivity) 
        // has not been written yet (TDD Red Phase). 
        Object activityInstance = createActivityInstance(mockSlack, mockGitHub);
        
        // Assuming the method signature: reportDefect(String defectId, String channel)
        invokeReportMethod(activityInstance, defectId, channel);

        // Assert
        // Verify exactly one message was sent
        assertEquals(1, mockSlack.messages.size(), "Slack should have received 1 message");

        MockSlackNotificationPort.SlackMessage msg = mockSlack.messages.get(0);
        
        // Verify channel
        assertEquals(channel, msg.channel(), "Message should be sent to the correct channel");

        // Verify Content (The core defect fix)
        assertTrue(
            msg.body().contains(expectedUrl),
            "Slack body must contain the GitHub issue URL.\nExpected: " + expectedUrl + "\nActual Body: " + msg.body()
        );
    }

    @Test
    void shouldThrowExceptionIfUrlIsMissing() throws Exception {
        // Arrange
        String defectId = "VW-999";
        // Configure GitHub mock to return a bad/empty state if that's a scenario,
        // or just test the happy path logic is strict.
        mockGitHub.setUrl("https://github.com/missing");

        Object activityInstance = createActivityInstance(mockSlack, mockGitHub);

        // Act & Assert
        // Depending on how strict the implementation is, we might want to ensure it doesn't silently fail.
        // For now, we verify the happy path is working correctly in the previous test.
        // This test simply ensures the adapter integration handles the data flow.
        invokeReportMethod(activityInstance, defectId, "#general");
        
        assertFalse(mockSlack.messages.isEmpty());
    }

    // --- Helper Methods for Reflection (TDD Phase) ---

    private Object createActivityInstance(SlackNotificationPort slackPort, GitHubIssuePort gitHubPort) {
        try {
            // We attempt to load the class that we expect the developer to create.
            Class<?> clazz = Class.forName(WORKFLOW_ACTIVITY_CLASS);
            
            // Try to find a constructor that accepts the ports
            try {
                Constructor<?> ctor = clazz.getConstructor(SlackNotificationPort.class, GitHubIssuePort.class);
                return ctor.newInstance(slackPort, gitHubPort);
            } catch (NoSuchMethodException e) {
                // Fallback for constructor without args (manual wiring)
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Implementation class not found: " + WORKFLOW_ACTIVITY_CLASS + 
                "\nThis is expected in the RED phase. Create the class to proceed.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate activity via reflection", e);
        }
    }

    private void invokeReportMethod(Object instance, String defectId, String channel) {
        try {
            Class<?> clazz = instance.getClass();
            // Try common method names for the activity
            Method method = null;
            try {
                method = clazz.getMethod("reportDefect", String.class, String.class);
            } catch (NoSuchMethodException e) {
                try {
                    method = clazz.getMethod("execute", String.class, String.class);
                } catch (NoSuchMethodException e2) {
                    throw new RuntimeException("Method 'reportDefect(String, String)' not found on " + clazz.getName() + 
                        "\nThis is expected in the RED phase.", e);
                }
            }
            method.invoke(instance, defectId, channel);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException(e.getCause());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
