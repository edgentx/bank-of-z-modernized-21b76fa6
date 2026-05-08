package com.example.domain.defect;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ override

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for Story S-FB-1.
 * Validates VW-454: GitHub URL in Slack body.
 *
 * Expected Behavior:
 * Triggering report_defect via temporal-worker exec results in Slack body containing GitHub issue link.
 */
@SpringBootTest(classes = DefectReportingTestConfig.class)
class VW454SlackLinkValidationTest {

    @Autowired
    private DefectReportingOrchestrator orchestrator;

    @Autowired
    private MockSlackNotificationPort slackMock;

    @Autowired
    private MockGitHubPort gitHubMock;

    @BeforeEach
    void setUp() {
        slackMock.reset();
        gitHubMock.setReturnEmpty(false);
        // Default mock URL
        gitHubMock.setMockUrl("https://github.com/project/issues/454");
    }

    /**
     * AC: Regression test added to e2e/regression/ covering this scenario.
     * Test: When a defect is reported, the Slack message body must contain the GitHub URL.
     */
    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String defectId = "S-FB-1";
        String issueId = "VW-454";
        String expectedUrl = "https://github.com/project/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, issueId, "GitHub URL missing in Slack body");

        // Act
        orchestrator.report(cmd);

        // Assert
        assertNotNull(slackMock.getLastMessageBody(), "Slack message should have been sent");
        
        String slackText = slackMock.getLastMessageText();
        assertNotNull(slackText, "Slack body text should not be null");
        
        // This assertion validates the fix for the defect
        assertTrue(
            slackText.contains(expectedUrl),
            "Slack body should contain the GitHub issue link: " + expectedUrl + "\nActual: " + slackText
        );
        
        // Verify structure (standard Slack message format)
        assertTrue(slackText.contains("GitHub issue"), "Body should reference 'GitHub issue'");
    }

    /**
     * Edge Case: GitHub URL is not found by the adapter.
     * Ensure the system fails gracefully or indicates missing info.
     */
    @Test
    void shouldHandleMissingGitHubUrl_gracefully() {
        // Arrange
        gitHubMock.setReturnEmpty(true);
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "VW-999", "Missing Issue");

        // Act & Assert
        // Implementation expectation: If URL is missing, specific placeholder or exception handling occurs.
        // For TDD, we verify the behavior.
        orchestrator.report(cmd);
        
        String text = slackMock.getLastMessageText();
        // Depending on implementation strategy, this might say "URL unavailable" or similar.
        assertNotNull(text, "Message should still be sent");
        assertFalse(text.contains("https://"), "Should not contain a link if none was found");
    }
}
