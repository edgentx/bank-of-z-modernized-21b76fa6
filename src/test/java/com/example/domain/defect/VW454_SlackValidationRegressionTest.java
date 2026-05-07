package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * Validates that when a defect is reported, the resulting event payload
 * contains the formatted GitHub URL for the Slack body.
 */
class VW454_SlackValidationRegressionTest {

    private DefectAggregate aggregate;
    private ReportDefectCmd cmd;

    @BeforeEach
    void setUp() {
        aggregate = new DefectAggregate("defect-454");
    }

    @Test
    void shouldContainGitHubUrlInMessageBody_WhenUrlIsPresent() {
        // Given: A command with a valid GitHub URL
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        cmd = new ReportDefectCmd(
                "defect-454",
                "GitHub URL missing in Slack",
                "Url is not rendering",
                "LOW",
                expectedUrl,
                Map.of("project", "21b76fa6")
        );

        // When: The command is executed
        var events = aggregate.execute(cmd);

        // Then: One event is produced
        assertNotNull(events);
        assertEquals(1, events.size());

        // And: The event message body contains the GitHub issue link
        var event = events.get(0);
        String messageBody = event.messageBody();

        assertNotNull(messageBody, "Message body should not be null");
        
        // Critical assertion for VW-454
        // Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(
            messageBody.contains("GitHub Issue: <" + expectedUrl + "|Link>"),
            "Slack body should contain formatted GitHub link: 'GitHub Issue: <url|Link>'. Got: " + messageBody
        );
    }

    @Test
    void shouldHandleMissingGitHubUrl_Gracefully() {
        // Edge case: Command without URL
        cmd = new ReportDefectCmd(
                "defect-455",
                "Generic Error",
                "No link provided",
                "HIGH",
                null, // No URL
                Map.of()
        );

        var events = aggregate.execute(cmd);
        
        // Should not throw exception, but body might differ
        assertEquals(1, events.size());
        String messageBody = events.get(0).messageBody();
        assertFalse(messageBody.contains("GitHub Issue:"));
    }
}