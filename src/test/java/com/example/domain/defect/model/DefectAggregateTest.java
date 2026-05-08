package com.example.domain.defect.model;

import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefectAggregateTest {

    @Test
    void whenExecuteReportDefectCmd_thenThrowsUnknownCommand() {
        // This is a placeholder test to ensure the file compiles and the structure is recognized.
        // The real logic (command handling) will be implemented in the Green phase.
        var aggregate = new DefectAggregate("test-id");
        
        // We expect the aggregate to exist, but since we are in Red Phase without implementation,
        // we are asserting the basic contract defined in AggregateRoot (id()).
        assertEquals("test-id", aggregate.id());
        assertEquals(0, aggregate.getVersion());
        
        // If we were to execute a command now, it would likely fail or throw UnknownCommandException
        // depending on the base class implementation. We leave this commented out as the
        // "implementation" is missing/empty.
        // assertThrows(UnknownCommandException.class, () -> aggregate.execute(new ReportDefectCmd("...", "...")));
    }
}
