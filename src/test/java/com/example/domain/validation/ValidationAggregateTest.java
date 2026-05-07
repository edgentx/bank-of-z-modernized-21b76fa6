package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationAggregate.
 * Covers logic for handling defect reports and URL persistence.
 */
class ValidationAggregateTest {

    @Test
    void shouldThrowWhenUrlMissing() {
        var agg = new ValidationAggregate("v1");
        var cmd = new ReportDefectCmd("v1", "Bug", "Desc", ""); // Blank URL
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            agg.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("required"));
    }

    @Test
    void shouldStoreUrlWhenValid() {
        var agg = new ValidationAggregate("v1");
        String url = "https://github.com/egdcrypto/bank-of-z/issues/454";
        var cmd = new ReportDefectCmd("v1", "Bug", "Desc", url);

        var events = agg.execute(cmd);

        assertFalse(events.isEmpty());
        assertEquals(url, agg.getExternalTicketUrl());
        
        var event = (DefectReportedEvent) events.get(0);
        assertEquals(url, event.githubIssueUrl());
    }

    @Test
    void shouldRejectUnknownCommand() {
        var agg = new ValidationAggregate("v1");
        var cmd = new Object() {}; // Not a recognized command
        
        assertThrows(UnknownCommandException.class, () -> {
            agg.execute(cmd);
        });
    }
}
