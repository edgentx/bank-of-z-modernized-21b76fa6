package com.example.domain.vforce360;

import com.example.adapters.DefectValidationAdapter;
import com.example.adapters.WebhookSlackNotificationAdapter;
import com.example.domain.shared.ValidationException;
import com.example.domain.shared.ValidationPort;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.services.DefectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VW-454: Regression test ensuring GitHub URL appears in Slack body end-to-end.
 * 
 * This test verifies:
 * 1. The validation service rejects defects without a GitHub URL.
 * 2. The Slack adapter correctly formats the GitHub URL into the body.
 */
@SpringBootTest
@ContextConfiguration(classes = {
    DefectService.class,
    DefectValidationAdapter.class,
    WebhookSlackNotificationAdapter.class
})
public class VForce360RegressionTest {

    @Autowired
    private DefectService defectService;

    @Autowired
    private WebhookSlackNotificationAdapter slackAdapter;

    @Test
    void testReportDefect_WithValidGitHubUrl_ShouldPassValidation() {
        // Arrange
        String validUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        var cmd = new ReportDefectCommand(
            "defect-454",
            "VW-454: GitHub URL Missing",
            "Slack body does not contain the link",
            "LOW",
            Map.of("github_issue_url", validUrl)
        );

        // Act & Assert
        assertDoesNotThrow(() -> defectService.reportDefect(cmd));
    }

    @Test
    void testReportDefect_WithMissingGitHubUrl_ShouldThrowValidationException() {
        // Arrange - Missing URL entirely
        var cmd = new ReportDefectCommand(
            "defect-455",
            "VW-455: No URL",
            "Metadata empty",
            "HIGH",
            Map.of() // Empty map
        );

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            defectService.reportDefect(cmd);
        });
        
        assertTrue(ex.getMessage().contains("GitHub URL is missing"));
    }

    @Test
    void testReportDefect_WithInvalidGitHubUrl_ShouldThrowValidationException() {
        // Arrange - Invalid URL format
        var cmd = new ReportDefectCommand(
            "defect-456",
            "VW-456: Bad URL",
            "Metadata has bad URL",
            "LOW",
            Map.of("github_issue_url", "not-a-url")
        );

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            defectService.reportDefect(cmd);
        });
        
        assertTrue(ex.getMessage().contains("invalid"));
    }

    @Test
    void testSlackAdapter_ShouldIncludeUrlInBody() {
        // Arrange
        String url = "https://github.com/egdcrypto/bank-of-z/issues/454";
        Map<String, String> metadata = Map.of("github_issue_url", url);

        // Act
        String body = slackAdapter.validateAndFormat("fb-1", "Fix Defect", metadata);

        // Assert - Explicitly checking for the Slack link format <url|text> or raw url
        // The defect states: "Slack body includes GitHub issue: <url>"
        // We verify the URL is present.
        assertTrue(body.contains(url), "Slack body must contain the GitHub URL");
        assertTrue(body.contains("Issue:"), "Slack body must label the link");
    }
}
