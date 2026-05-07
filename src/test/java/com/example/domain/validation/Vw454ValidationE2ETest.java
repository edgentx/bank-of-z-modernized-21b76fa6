package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that the Slack notification body generated during defect reporting
 * includes the link to the GitHub issue.
 */
class Vw454ValidationE2ETest {

    private MockSlackNotificationPort mockSlack;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        // We mock the Slack adapter to inspect the output payload
        mockSlack = new MockSlackNotificationPort();
        // We inject the mock. The real implementation likely uses @Autowired, 
        // but for unit testing we construct manually or rely on reflection.
        // Here we assume a Service constructor or setter injection pattern exists or is implied.
        validationService = new ValidationService(mockSlack);
    }

    /**
     * Acceptance Criterion: Regression test added to e2e/regression/ covering this scenario.
     * Scenario:
     * 1. Trigger _report_defect (simulated by calling handle)
     * 2. Verify Slack body contains GitHub issue link.
     *
     * Expected: Slack body includes GitHub issue: <url>
     */
    @Test
    void testReportDefect_shouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String githubUrl = "https://github.com/example/project/issues/123";
        
        ReportDefectCmd command = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454 — GitHub URL in Slack body",
            "Severity: LOW...",
            githubUrl
        );

        // Act
        List<DefectReportedEvent> events = validationService.handleReportDefect(command);

        // Assert
        assertFalse(mockSlack.getSentMessages().isEmpty(), "Slack notification should have been triggered");
        
        String actualSlackBody = mockSlack.getLastMessage();
        
        // The critical assertion: The URL must be present in the message body sent to Slack
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(githubUrl), 
            "Slack body must contain the GitHub issue URL. Expected to contain: " + githubUrl + "\nActual: " + actualSlackBody
        );
        
        // Verify event structure also reflects the reality
        assertEquals(1, events.size());
        assertEquals(defectId, events.get(0).defectId());
    }

    @Test
    void testReportDefect_shouldFailIfUrlIsMissing() {
        // Arrange
        String defectId = "VW-454-NO-URL";
        String githubUrl = null; // Missing URL

        ReportDefectCmd command = new ReportDefectCmd(
            defectId,
            "Fix: Validating...",
            "Severity: LOW...",
            githubUrl
        );

        // Act & Assert
        // We expect the service to handle this gracefully or throw a specific validation error.
        // Given "validation" component, we expect a failure if the URL is missing.
        assertThrows(IllegalArgumentException.class, () -> {
            validationService.handleReportDefect(command);
        });
    }
}
