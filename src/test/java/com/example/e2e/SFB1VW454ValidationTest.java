package com.example.e2e;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.ports.VForce360NotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1: Validating VW-454.
 * Ensures that the GitHub URL is present in the Slack body when a defect is reported.
 * 
 * Test Mode: Red (Tests are written, implementation is assumed missing or broken).
 */
class SFB1VW454ValidationTest {

    private VForce360NotificationPort slackPort;
    private Object aggregate; // Using Object because we don't have the Aggregate class yet

    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/issues/454";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Setup Mock Adapter
        slackPort = new MockSlackNotificationPort();

        // 2. Instantiate Aggregate (Reflection to survive compilation without the class existing)
        try {
            Class<?> clazz = Class.forName("com.example.domain.vforce360.model.VForce360Aggregate");
            aggregate = clazz.getConstructor(String.class, VForce360NotificationPort.class)
                    .newInstance("test-aggregate-id", slackPort);
        } catch (ClassNotFoundException e) {
            // In strict TDD Red, the class might not exist yet.
            // We create a dummy proxy to allow the test structure to exist,
            // but the test assertions will naturally fail or throw exceptions.
            System.out.println("WARN: VForce360Aggregate not found yet. This is expected in Red phase.");
            aggregate = new Object();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() throws Exception {
        // Given: A command to report a defect with a specific GitHub URL
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "GitHub URL missing",
            "Slack body does not contain the link",
            EXPECTED_GITHUB_URL
        );

        // When: The aggregate processes the command
        List<Object> events = List.of();
        try {
            // We expect the aggregate to have an 'execute' method
            Method executeMethod = aggregate.getClass().getMethod("execute", ReportDefectCmd.class);
            events = (List<Object>) executeMethod.invoke(aggregate, cmd);
        } catch (NoSuchMethodException e) {
            fail("Aggregate 'execute' method not found. Implementation missing.");
        }

        // Then: 1. An event is produced
        assertFalse(events.isEmpty(), "Expected a DefectReportedEvent to be produced");

        // Then: 2. The Slack port was called (implicitly via the aggregate or a handler)
        // Note: In a real flow, a synchronous handler might listen to the event.
        // For this E2E test, we verify the side-effect captured by the Mock.
        
        // We simulate the handler execution here if the aggregate didn't do it directly.
        // Or we assume the Aggregate calls the Port directly (Anemic domain, but common in batch).
        // Let's check the mock.
        
        boolean found = ((MockSlackNotificationPort) slackPort).wasUrlPosted(EXPECTED_GITHUB_URL);

        // This assertion is the core of VW-454 validation.
        assertTrue(
            found,
            "Slack body should contain the GitHub issue URL: " + EXPECTED_GITHUB_URL + ". " +
            "This validation failed. Check the generated message body in the mock."
        );
    }

    @Test
    void shouldThrowExceptionIfGitHubUrlIsMissing() throws Exception {
        // Given: A command without a GitHub URL
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455",
            "Missing URL",
            "No URL provided",
            null // Violation of business rules
        );

        // When/Then: Expect exception
        try {
            Method executeMethod = aggregate.getClass().getMethod("execute", ReportDefectCmd.class);
            executeMethod.invoke(aggregate, cmd);
            fail("Expected IllegalArgumentException for missing GitHub URL, but execution succeeded.");
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                // Expected behavior
                assertTrue(cause.getMessage().contains("githubUrl") || cause.getMessage().contains("GitHub"));
            } else {
                fail("Expected IllegalArgumentException, got: " + cause);
            }
        }
    }
}
