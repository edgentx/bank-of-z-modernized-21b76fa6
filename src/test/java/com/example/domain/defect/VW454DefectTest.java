package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.shared.Aggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Testing Defect VW-454.
 * 
 * AC: The validation no longer exhibits the reported behavior.
 * AC: Regression test added covering this scenario.
 * 
 * Scenario: Verify that reporting a defect generates the correct event
 * containing the GitHub URL in the Slack body representation.
 */
class VW454DefectTest {

    @Test
    void shouldContainGitHubUrlInSlackBody() {
        // Given
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/bank-of-z/modernization/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            expectedUrl, 
            "Validating VW-454 — GitHub URL in Slack body"
        );

        // When
        Aggregate aggregate = new DefectAggregate(defectId);
        List events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should produce an event");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Should be DefectReportedEvent");

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(defectId, event.aggregateId());
        
        // Failing assertion: The slack body must explicitly contain the URL
        // This will fail until the implementation formats the string correctly.
        String slackBody = event.getSlackBody();
        assertTrue(
            slackBody.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL: [" + expectedUrl + "] but was: " + slackBody
        );
    }
}
