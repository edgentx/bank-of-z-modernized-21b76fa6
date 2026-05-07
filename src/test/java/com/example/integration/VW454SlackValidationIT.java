package com.example.integration;

import com.example.adapters.SlackNotificationPort;
import com.example.domain.shared.Command;
import com.example.domain.report.model.ReportDefectCmd;
import com.example.domain.report.model.ReportAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * VW-454 Regression Test.
 * Defect: GitHub URL in Slack body (end-to-end).
 *
 * Tests the Temporal workflow / Saga orchestration layer (Simulated here via direct application of
 * the ReportAggregate and its notification wiring) to ensure that when a defect is reported,
 * the resulting Slack notification body contains the link to the created GitHub issue.
 */
@SpringBootTest(classes = VW454SlackValidationIT.TestConfig.class)
public class VW454SlackValidationIT {

    @Autowired
    private ReportAggregate reportAggregate;

    @Autowired
    private MockGitHubPort gitHubPort;

    @Autowired
    private MockSlackPort slackPort;

    @BeforeEach
    void setUp() {
        // Reset mock state before each test to ensure isolation
        gitHubPort.reset();
        slackPort.reset();
    }

    /**
     * AC: The validation no longer exhibits the reported behavior.
     * Scenario: Triggering a defect report via temporal-worker exec (simulated).
     * Expectation: Verify Slack body contains GitHub issue link.
     */
    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenDefectReported() {
        // Given
        String defectDescription = "E2E Validation Failure for VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd("issue-454", defectDescription);

        // Configure the mock GitHub adapter to return a specific URL
        URI expectedGitHubUrl = URI.create("https://github.com/bank-of-z/issues/454");
        gitHubPort.setNextCreateIssueUrl(expectedGitHubUrl);

        // When
        // In the real system, Temporal would call this. In the test, we call the aggregate directly.
        // The aggregate emits the event, which the test listener or test framework would catch.
        // Here we verify the Aggregate logic + the downstream port interactions.
        List<com.example.domain.shared.DomainEvent> events = reportAggregate.execute(cmd);

        // Apply the event to the aggregate (simulation of state change)
        events.forEach(e -> reportAggregate.apply(e));

        // Then
        // 1. Verify GitHub was called
        assertTrue(gitHubPort.wasCreateIssueCalled(), "GitHub issue creation should have been triggered");
        assertEquals(defectDescription, gitHubPort.getLastCapturedDescription());

        // 2. Retrieve the generated URL
        String actualUrl = gitHubPort.getLastCreatedUrl().toString();

        // 3. CRITICAL ASSERTION: Verify Slack was called with the URL in the body
        assertTrue(slackPort.wasNotifyCalled(), "Slack notification should have been triggered");
        
        String slackBody = slackPort.getLastMessageBody();
        assertNotNull(slackBody, "Slack message body should not be null");
        
        // VW-454 Check: The body must contain the GitHub URL
        assertTrue(
            slackBody.contains(actualUrl),
            "Slack body must contain the GitHub issue URL.\nExpected to contain: " + actualUrl + "\nActual Body: " + slackBody
        );
    }

    @Test
    void shouldFailValidationIfSlackBodyMissingUrl() {
        // Simulate a scenario where the URL generation succeeded (Mock returns valid URL)
        // but the formatting logic failed to include it in the Slack payload.
        // This acts as a regression test for a specific implementation bug.
        
        // Given
        String defectDescription = "Regression Test Case";
        ReportDefectCmd cmd = new ReportDefectCmd("regression-1", defectDescription);
        
        URI expectedGitHubUrl = URI.create("https://github.com/bank-of-z/regression/1");
        gitHubPort.setNextCreateIssueUrl(expectedGitHubUrl);

        // When
        reportAggregate.execute(cmd);

        // Then
        String slackBody = slackPort.getLastMessageBody();
        
        // If the body is empty or missing the link, the assertion fails, indicating the defect exists.
        if (slackBody == null || !slackBody.contains(expectedGitHubUrl.toString())) {
            fail("VW-454 REGRESSION: Slack body did not contain the GitHub URL.");
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ReportAggregate reportAggregate(GitHubPort gitHubPort, SlackPort slackPort) {
            return new ReportAggregate(gitHubPort, slackPort);
        }

        @Bean
        public GitHubPort gitHubPort() {
            return new MockGitHubPort();
        }

        @Bean
        public SlackPort slackPort() {
            return new MockSlackPort();
        }
    }
}
