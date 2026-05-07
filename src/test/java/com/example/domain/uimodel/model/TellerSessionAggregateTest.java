package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TellerSessionAggregateTest {

    @Test
    void shouldStartSession() {
        String id = "session-1";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        StartSessionCmd cmd = new StartSessionCmd(id, "teller-1", "term-1");

        List<DomainEvent> events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        assertTrue(aggregate.isActive());
        assertEquals("teller-1", aggregate.getTellerId());
        assertEquals("term-1", aggregate.getTerminalId());
    }

    @Test
    void shouldFailIfAlreadyActive() {
        String id = "session-2";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        aggregate.execute(new StartSessionCmd(id, "teller-1", "term-1"));

        // Try to start again
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(new StartSessionCmd(id, "teller-1", "term-1"));
        });

        assertTrue(ex.getMessage().contains("already active"));
    }

    @Test
    void shouldFailWithInvalidTellerId() {
        String id = "session-3";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        StartSessionCmd cmd = new StartSessionCmd(id, "", "term-1");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Teller ID"));
    }

    @Test
    void shouldFailWithInvalidTerminalId() {
        String id = "session-4";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(id);
        StartSessionCmd cmd = new StartSessionCmd(id, "teller-1", "");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Terminal ID"));
    }
}
