package e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.VForce360RepositoryPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Regression test for GitHub URL validation in Slack body.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Expected: The aggregate enforces that a GitHub URL is present.
 */
public class SFB1DefectUrlValidationTest {

    @Test
    public void testReportDefect_RequiresGithubUrl() {
        // Given
        String defectId = "S-FB-1-test-1";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Test Defect",
            "Description",
            DefectAggregate.Severity.LOW,
            "validation",
            "https://github.com/example/issues/454"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof com.example.domain.defect.model.DefectReportedEvent);
        
        com.example.domain.defect.model.DefectReportedEvent event = 
            (com.example.domain.defect.model.DefectReportedEvent) events.get(0);
        assertEquals("https://github.com/example/issues/454", event.githubIssueUrl());
    }

    @Test
    public void testReportDefect_FailsIfUrlIsMissing() {
        // Given
        String defectId = "S-FB-1-test-2";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Test Defect",
            "Description",
            DefectAggregate.Severity.LOW,
            "validation",
            null // URL is null
        );

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub Issue URL required"));
    }

    @Test
    public void testReportDefect_FailsIfUrlIsBlank() {
        // Given
        String defectId = "S-FB-1-test-3";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Test Defect",
            "Description",
            DefectAggregate.Severity.LOW,
            "validation",
            "   " // URL is blank
        );

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub Issue URL required"));
    }
}