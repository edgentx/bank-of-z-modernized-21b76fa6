package com.example.domain.diagnostic;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Verification of VW-454 defect fix.
 * Story: S-FB-1
 * 
 * Validates that the ReportDefectCommand generates a proper event payload
 * specifically containing the GitHub URL. This serves as the unit-level
 * assertion backing the end-to-end regression test.
 */
class DiagnosticReportTest {

    private static final String ISSUE_ID = "VW-454";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/modernization/issues/VW-454";

    private DiagnosticAggregate aggregate;

    @BeforeEach
    void setUp() {
        // Initialize aggregate with the specific issue ID from the defect report
        aggregate = new DiagnosticAggregate(ISSUE_ID);
    }

    @Test
    void handleUnknownCommandThrowsException() {
        // Given an unknown command
        Command unknownCmd = new Command() {}; // Anonymous mock command

        // When execute is called
        // Then it should throw UnknownCommandException
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    @Test
    void reportDefectGeneratesEventWithGitHubUrl() {
        // Given a ReportDefectCommand with specific severity and component
        ReportDefectCmd cmd = new ReportDefectCmd(
            ISSUE_ID,
            "Validating VW-454",
            "The body is missing the GitHub URL link.",
            "LOW",
            "validation"
        );

        // When execute is called
        List events = aggregate.execute(cmd);

        // Then: Should return one event
        assertNotNull(events);
        assertEquals(1, events.size());

        // And: The event should be of the correct type
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);

        // CRITICAL ASSERTION FOR S-FB-1:
        // The event payload MUST contain the constructed GitHub URL to satisfy
        // the Slack body requirement expected by the downstreamTemporal worker.
        String slackBody = event.getSlackBody();
        assertNotNull(slackBody, "Slack body must not be null");
        
        // This assertion will FAIL until the implementation is fixed (Red Phase)
        assertTrue(
            slackBody.contains(EXPECTED_GITHUB_URL),
            "Slack body must contain the GitHub URL: " + EXPECTED_GITHUB_URL + ". Found: " + slackBody
        );
        
        // Also verify basic meta-data is present
        assertEquals(ISSUE_ID, event.getAggregateId());
        assertTrue(slackBody.contains("LOW"));
        assertTrue(slackBody.contains("validation"));
    }
}
