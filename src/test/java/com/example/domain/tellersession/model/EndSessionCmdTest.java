package com.example.domain.tellersession.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EndSessionCmdTest {
    @Test
    void testRecordInstantiation() {
        EndSessionCmd cmd = new EndSessionCmd("session-1");
        assertEquals("session-1", cmd.sessionId());
    }
}
