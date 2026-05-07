package com.example.domain.vforce360;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VForce360 Aggregate.
 * Covers AC: The validation no longer exhibits the reported behavior.
 */
public class VForce360AggregateTest {

    @Test
    void testSuccessfulDefectReportWithLink() {
        // Arrange
        VForce360Aggregate aggregate = new VForce360Aggregate("VW-454");
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        String slackBody = "Investigating VW-454. Link: " + githubUrl;
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", githubUrl, slackBody);

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty());
        assertTrue(aggregate.isReported());
        assertEquals(githubUrl, aggregate.getGithubUrl());
    }

    @Test
    void testValidationFailureWhenLinkIsMissingFromBody() {
        // Arrange
        VForce360Aggregate aggregate = new VForce360Aggregate("VW-454");
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        // Body does NOT contain the URL
        String slackBody = "Investigating VW-454.";
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", githubUrl, slackBody);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Validation Failed"));
        assertTrue(exception.getMessage().contains("Slack body must contain the GitHub Issue URL"));
        assertFalse(aggregate.isReported());
    }

    @Test
    void testValidationFailureWhenBodyIsEmpty() {
        // Arrange
        VForce360Aggregate aggregate = new VForce360Aggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "http://...", "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
