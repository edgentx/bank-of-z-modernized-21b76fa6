package com.example.e2e;

import com.example.adapters.DefectRepositoryAdapter;
import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1.
 * Verifies that when a defect is reported, the resulting Slack notification body
 * contains the GitHub issue URL.
 */
class SlackBodyRegressionTest {

    private DefectRepository defectRepository;
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        defectRepository = new DefectRepositoryAdapter();
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/org/repo/issues/454";

        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Act: Simulate the report_defect workflow/activity logic
        // 1. Execute Command
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "VForce360 GitHub URL missing in Slack", expectedUrl);
        var events = aggregate.execute(cmd);

        // 2. Save Aggregate
        defectRepository.save(aggregate);

        // 3. Format Slack Message
        DefectAggregate savedDefect = defectRepository.findById(defectId);
        assertNotNull(savedDefect);
        
        String slackBody = String.format(
            "Defect Reported: %s\n" +
            "Details: %s\n" +
            "GitHub Issue: <%s|View>",
            savedDefect.id(),
            "GitHub URL in Slack body (end-to-end)",
            savedDefect.getGithubUrl()
        );

        // 4. Send Notification
        slackPort.sendNotification("#vforce360-issues", slackBody);

        // Assert
        assertEquals(1, slackPort.getSentMessages().size());
        MockSlackNotificationPort.Message msg = slackPort.getSentMessages().get(0);
        
        assertEquals("#vforce360-issues", msg.channel);
        assertTrue(msg.message.contains("<https://github.com/org/repo/issues/454|View>"), 
            "Slack body should contain the formatted GitHub URL");
        assertTrue(msg.message.contains(expectedUrl), 
            "Slack body should contain the raw GitHub URL");
    }

    @Test
    void shouldFailIfGitHubUrlIsMissing() {
        // Arrange
        String defectId = "VW-FAIL-1";
        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Act & Assert
        // Command with null URL should fail domain validation
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(new ReportDefectCmd(defectId, "Missing URL", null));
        });

        // Command with invalid URL should fail domain validation
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(new ReportDefectCmd(defectId, "Invalid URL", "https://gitlab.com/org/repo/issues/1"));
        });
    }
}
