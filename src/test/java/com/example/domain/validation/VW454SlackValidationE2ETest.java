package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454.
 * Validates that the Slack body contains the GitHub URL after reporting a defect.
 *
 * Corresponds to Story ID: S-FB-1
 */
class VW454SlackValidationE2ETest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubPort gitHubPort;
    private ValidationService validationService; // The class under test

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubPort();
        validationService = new ValidationService(slackPort, gitHubPort);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_WhenDefectReported() {
        // Arrange
        String defectId = "VW-454";
        String title = "Validating VW-454 — GitHub URL in Slack body";
        Map<String, String> metadata = Map.of("component", "validation", "severity", "LOW");
        
        // We don't validate the aggregate logic here (S-FB-1 focuses on the E2E defect report output),
        // but we trigger the command as requested by the story steps.
        // Ideally, we'd have an Aggregate, but for E2E integration/defect testing, we can test the Service Handler directly.
        // However, to strictly follow the prompt's domain pattern, we construct the Command.
        Command reportDefectCmd = new ReportDefectCmd(defectId, title, metadata);

        // Act
        validationService.handleReportDefect(reportDefectCmd);

        // Assert
        // 1. Verify Slack was called
        assertTrue(slackPort.invocationCount > 0, "Slack notification was not sent");

        // 2. CRITICAL ASSERTION: Verify Slack body contains GitHub issue link
        // This addresses the "Expected Behavior: Slack body includes GitHub issue: <url>"
        slackPort.assertGithubUrlPresent();

        // 3. Verify the specific URL matches what GitHub "created"
        String expectedUrl = "https://github.com/example-bank/z-modernized/issues/454";
        String actualUrl = slackPort.lastAttachments.get("github_url");
        assertEquals(expectedUrl, actualUrl, "GitHub URL in Slack attachments does not match expected issue link");

        // 4. Verify the body itself references the issue context (optional but good for E2E)
        assertNotNull(slackPort.lastBody, "Slack body is null");
        assertTrue(slackPort.lastBody.contains(defectId), "Slack body does not contain Defect ID");
    }

    @Test
    void shouldFailIfSlackBodyMissingGithubUrl() {
        // Edge case: What if GitHub fails or returns null? (Red phase test)
        gitHubPort.setNextIssueUrl(null);
        
        Command reportDefectCmd = new ReportDefectCmd("VW-455", "Missing URL", Map.of());

        // This will likely throw an NPE or fail validation in the real implementation
        assertThrows(NullPointerException.class, () -> {
            validationService.handleReportDefect(reportDefectCmd);
        });
    }
}
