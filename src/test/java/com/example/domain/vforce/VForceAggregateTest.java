package com.example.domain.vforce;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce.model.DefectReportedEvent;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.domain.vforce.model.VForceAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VForceAggregateTest {

    @Test
    void shouldThrowUnknownCommandForNonReportCommands() {
        var aggregate = new VForceAggregate("test-id");
        Object badCmd = new Object(); // Not a ReportDefectCmd

        // The execute method takes a Command interface, so we pass something that isn't handled.
        // Note: In the real implementation we might create a valid Command interface instance
        // that isn't handled, but here we trigger the exception logic.
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(() -> "Unknown"));
    }
}
