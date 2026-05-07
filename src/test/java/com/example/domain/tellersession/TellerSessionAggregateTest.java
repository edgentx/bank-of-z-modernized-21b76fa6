package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TellerSessionAggregateTest {

    @Test
    void shouldHandleUnknownCommand() {
        var agg = new TellerSessionAggregate("TS-TEST");
        assertThrows(UnknownCommandException.class, () -> agg.execute(new Object() {}));
    }

    @Test
    void shouldRejectIfNotAuthenticated() {
        var agg = new TellerSessionAggregate("TS-UNAUTH");
        // isAuthenticated defaults to false or is set via test hook
        var cmd = new StartSessionCmd("TS-UNAUTH", "T1", "TM1");
        
        var ex = assertThrows(IllegalStateException.class, () -> agg.execute(cmd));
        assertTrue(ex.getMessage().contains("authenticated"));
    }
}
