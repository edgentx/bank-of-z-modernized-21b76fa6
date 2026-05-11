package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefectAggregateTest {

    @Test
    void shouldReportDefectAndGenerateGithubUrl() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd("defect-1", "Critical Bug", "Fix this", "HIGH");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = (DefectReportedEvent) events.get(0);
        assertEquals("defect-1", event.aggregateId());
        assertEquals("Critical Bug", event.title());
        assertNotNull(event.githubIssueUrl());
        assertTrue(event.githubIssueUrl().startsWith("https://github.com/egdcrypto/bank-of-z/issues/"));
    }

    @Test
    void shouldThrowOnDuplicateReport() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd("defect-1", "Bug", "Desc", "LOW");
        aggregate.execute(cmd);

        // When/Then
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void shouldThrowOnUnknownCommand() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var unknownCmd = new Object() implements com.example.domain.shared.Command {};

        // When/Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }

    @Test
    void shouldRequireTitle() {
        // Given
        var aggregate = new DefectAggregate("defect-1");
        var cmd = new ReportDefectCmd("defect-1", "", "Desc", "LOW");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
