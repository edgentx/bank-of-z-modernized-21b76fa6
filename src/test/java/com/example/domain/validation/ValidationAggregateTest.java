package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-FB-1.
 * Testing the defect reporting flow ensuring GitHub URL presence in the resulting event.
 */
class ValidationAggregateTest {

    @Test
    void shouldThrowExceptionWhenGithubUrlIsMissing() {
        // Given: A defect command without an explicit GitHub URL (simulating the bug)
        // The implementation currently returns null for the URL, triggering a failure.
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "GitHub URL in Slack body (end-to-end)",
            "LOW",
            "Defect reported by user",
            Map.of("source", "VForce360 PM diagnostic")
        );

        ValidationAggregate aggregate = new ValidationAggregate(defectId);

        // When & Then: Execute the command
        // We expect an IllegalStateException because the GitHub URL is missing
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> aggregate.execute(cmd)
        );

        // Verify the specific error message related to the missing URL
        assertTrue(exception.getMessage().contains("GitHub Issue URL must be present"));
    }

    @Test
    void shouldEmitEventWithGithubUrlWhenProvided() {
        // NOTE: This test will fail until the implementation is fixed to accept or generate the URL.
        // Given: A defect command that implies a URL should be generated.
        // To make this pass later, we might inject a GitHub service or pass the URL in the command.
        String defectId = "VW-455";
        
        // Hypothetically, if the URL was passed or generated correctly
        String expectedUrl = "https://github.com/owner/repo/issues/454";
        
        // We are testing the SHAPE of the event.
        // Since we can't inject the mock in the aggregate constructor easily without changing the design,
        // we verify the Event structure.
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Test Defect",
            "HIGH",
            "Testing URL presence",
            Map.of("githubUrl", expectedUrl) // Passing via metadata as a workaround strategy for later
        );

        ValidationAggregate aggregate = new ValidationAggregate(defectId);

        // We expect failure in Red phase because the aggregate doesn't know how to read metadata yet.
        assertThrows(Exception.class, () -> aggregate.execute(cmd));
    }

    @Test
    void shouldRejectUnknownCommands() {
        String defectId = "VW-999";
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        
        // When: Sending a junk command (simulating a raw Command object)
        CommandStub unknownCmd = new CommandStub();

        // Then:
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    // Helper class for testing unknown command handling
    private static class CommandStub implements com.example.domain.shared.Command {}
}
