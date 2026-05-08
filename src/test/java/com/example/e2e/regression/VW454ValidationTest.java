package com.example.e2e.regression;

import com.example.domain.shared.ValidationFailedException;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackPort;
import com.example.mocks.MockTemporalDefectPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ID: S-FB-1
 * Title: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Regression test covering the scenario where a defect report must contain
 * the GitHub issue URL in the Slack notification body.
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * Actual Behavior (Red Phase): Validation logic/implementation is missing/incomplete.
 */
public class VW454ValidationTest {

    private static final String VALIDATION_ID = "vforce360-1";
    private MockSlackPort slackPort;
    private MockTemporalDefectPort temporalPort;
    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackPort();
        temporalPort = new MockTemporalDefectPort();
        aggregate = new ValidationAggregate(VALIDATION_ID);
    }

    @Test
    void whenReportingDefect_SlackBodyShouldContainGitHubUrl() {
        // Arrange
        // Simulating the Temporal worker exec triggering the report
        String defectId = "VW-454";
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "GitHub URL missing in Slack body",
                "LOW",
                Map.of("githubUrl", githubUrl) // Assuming the command/context carries this info
        );

        // Act
        // In the actual flow, the Temporal worker calls the aggregate/handler.
        // Here we invoke the aggregate logic directly with the mock port.
        List<DefectReportedEvent> events = aggregate.execute(cmd, slackPort);

        // Assert
        // 1. Verify the event was produced
        assertFalse(events.isEmpty(), "DefectReportedEvent should be produced");
        assertEquals(defectId, events.get(0).defectId(), "Event should contain correct defect ID");

        // 2. Verify the side effect: Slack was called
        assertNotNull(slackPort.lastBody, "Slack body should not be null");
        assertEquals("#vforce360-issues", slackPort.lastChannel, "Slack channel should be vforce360-issues");

        // 3. CRITICAL ASSERTION for VW-454
        // The defect is that the URL is missing. We assert that it IS present.
        // This will fail in the Red phase because the implementation stub in ValidationAggregate
        // does not include the URL logic.
        boolean containsUrl = slackPort.lastBody.contains(githubUrl);
        assertTrue(containsUrl, 
                "Slack body must contain the GitHub issue URL (" + githubUrl + "). " +
                "Actual body: " + slackPort.lastBody);
    }

    @Test
    void whenReportingDefect_multipleReportsShouldFail() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-455",
                "Duplicate Report",
                "LOW",
                Map.of()
        );

        // Act
        aggregate.execute(cmd, slackPort);

        // Assert
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd, slackPort);
        }, "Should not be able to report the same defect twice on the same aggregate");
    }
}
