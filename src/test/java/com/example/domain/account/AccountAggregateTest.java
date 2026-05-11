package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountAggregateTest {

    @Test
    public void test_close_account_success() {
        AccountAggregate agg = new AccountAggregate("ACC-1");
        agg.setState("ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO);
        
        List<DomainEvent> events = agg.execute(new CloseAccountCmd("ACC-1"));
        
        assertEquals(1, events.size());
        assertEquals("account.closed", events.get(0).type());
    }

    @Test
    public void test_close_account_fails_if_balance_above_minimum() {
        AccountAggregate agg = new AccountAggregate("ACC-2");
        agg.setState("ACTIVE", new BigDecimal("100"), new BigDecimal("10"));
        
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            agg.execute(new CloseAccountCmd("ACC-2"));
        });
        
        assertTrue(ex.getMessage().contains("Balance is above minimum"));
    }
}