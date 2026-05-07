package com.example.domain.tellersession;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TellerSessionAggregateTest {

    @Test
    void testExecute_StartSession_Success() {
        TellerSession agg = new TellerSession("session-1");
        StartSessionCmd cmd = new StartSessionCmd("teller-1", "terminal-1", true);

        List<com.example.domain.shared.DomainEvent> events = agg.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        assertEquals("session-1", events.get(0).aggregateId());
    }

    @Test
    void testExecute_StartSession_Unauthenticated() {
        TellerSession agg = new TellerSession("session-2");
        // Command indicates authentication failed or not present
        StartSessionCmd cmd = new StartSessionCmd("teller-2", "terminal-2", false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> agg.execute(cmd));
        assertTrue(ex.getMessage().contains("authenticated"));
    }

    @Test
    void testExecute_UnknownCommand() {
        TellerSession agg = new TellerSession("session-3");
        Command unknownCmd = new Command() {};

        assertThrows(UnknownCommandException.class, () -> agg.execute(unknownCmd));
    }
}
