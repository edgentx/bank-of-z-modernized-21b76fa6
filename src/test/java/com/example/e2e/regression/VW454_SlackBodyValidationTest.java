package com.example.e2e.regression;

import com.example.domain.report_defect.model.ReportDefectCommand;
import com.example.mocks.InMemorySlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454: GitHub URL in Slack body.
 * 
 * Story: S-FB-1
 * Context: VForce360 defect reporting via temporal-worker.
 * 
 * Expected: Slack body includes GitHub issue link <url>.
 */
@SpringBootTest
public class VW454_SlackBodyValidationTest {

    @Autowired
    private InMemorySlackNotificationAdapter slackAdapter;

    @Autowired
    private SlackNotificationPort slackPort;

    @BeforeEach
    public void setup() {
        slackAdapter.clear();
    }

    /**
     * Test Case 1: Verify that when a defect is reported,
     * the generated Slack body contains a valid GitHub URL.
     */
    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId,
            "GitHub URL in Slack body",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            Map.of("traceId", "trace-123")
        );

        // Act
        String result = slackPort.sendDefectNotification(cmd);

        // Assert
        assertNotNull(result, "Slack body should not be null");
        
        // Validate the Expected Behavior: "Slack body includes GitHub issue: <url>"
        // We check for the presence of the GitHub URL pattern or the specific label.
        boolean hasGithubLabel = result.contains("GitHub Issue:");
        boolean hasGithubLink = result.contains("https://github.com/");

        assertTrue(hasGithubLabel, "Slack body must contain the label 'GitHub Issue:'");
        assertTrue(hasGithubLink, "Slack body must contain a GitHub URL");
    }

    /**
     * Test Case 2: Verify the format matches the defect ID correctly.
     */
    @Test
    void testSlackBodyFormatIsCorrect() {
        // Arrange
        String defectId = "VW-999";
        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId, 
            "Another defect", 
            "HIGH", 
            "core", 
            "proj-xyz", 
            Map.of()
        );

        // Act
        String result = slackPort.sendDefectNotification(cmd);

        // Assert
        // The implementation should ideally link the specific issue ID
        assertTrue(result.contains(defectId), "Slack body should reference the specific Defect ID: " + defectId);
    }
}
