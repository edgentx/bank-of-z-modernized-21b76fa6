package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountAggregateTest {

    @Test
    public void testCloseAccountSuccess() {
        String id = "acc-1";
        AccountAggregate agg = new AccountAggregate(id);
        agg.setState("12345", BigDecimal.ZERO, "ACTIVE");

        CloseAccountCmd cmd = new CloseAccountCmd(id, "12345");
        var events = agg.execute(cmd);

        assertFalse(events.isEmpty());
        assertEquals("account.closed", events.get(0).type());
        assertEquals("CLOSED", agg.getStatus());
    }

    @Test
    public void testCloseAccountFailsIfBalanceNotZero() {
        String id = "acc-2";
        AccountAggregate agg = new AccountAggregate(id);
        agg.setState("12345", new BigDecimal("100.00"), "ACTIVE");

        CloseAccountCmd cmd = new CloseAccountCmd(id, "12345");
        
        Exception ex = assertThrows(IllegalStateException.class, () -> agg.execute(cmd));
        assertTrue(ex.getMessage().contains("balance must be zero"));
    }

    @Test
    public void testCloseAccountFailsIfNotActive() {
        String id = "acc-3";
        AccountAggregate agg = new AccountAggregate(id);
        agg.setState("12345", BigDecimal.ZERO, "SUSPENDED");

        CloseAccountCmd cmd = new CloseAccountCmd(id, "12345");

        Exception ex = assertThrows(IllegalStateException.class, () -> agg.execute(cmd));
        assertTrue(ex.getMessage().contains("must be ACTIVE"));
    }

    @Test
    public void testCloseAccountFailsImmutabilityCheck() {
        String id = "acc-4";
        AccountAggregate agg = new AccountAggregate(id);
        // Set initial state
        agg.setState("ORIGINAL", BigDecimal.ZERO, "ACTIVE");

        // Try to close with a DIFFERENT account number
        CloseAccountCmd cmd = new CloseAccountCmd(id, "DIFFERENT");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> agg.execute(cmd));
        assertTrue(ex.getMessage().contains("immutable"));
    }
    
    @Test
    public void testUnknownCommand() {
        AccountAggregate agg = new AccountAggregate("acc-5");
        assertThrows(UnknownCommandException.class, () -> agg.execute(new Command() {}));
    }
}
