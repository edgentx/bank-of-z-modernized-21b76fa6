package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    @Test
    void closeAccount_success() {
        AccountAggregate account = new AccountAggregate("ACC-001");
        account.load(BigDecimal.ZERO, AccountAggregate.Status.ACTIVE);

        List<DomainEvent> events = account.execute(new CloseAccountCmd("ACC-001"));

        assertEquals(1, events.size());
        assertEquals("account.closed", events.get(0).type());
        assertTrue(account.isClosed());
    }

    @Test
    void closeAccount_failsIfBalanceNotZero() {
        AccountAggregate account = new AccountAggregate("ACC-002");
        account.setBalance(BigDecimal.TEN);
        account.setStatus(AccountAggregate.Status.ACTIVE);

        Exception ex = assertThrows(IllegalStateException.class, () -> {
            account.execute(new CloseAccountCmd("ACC-002"));
        });

        assertTrue(ex.getMessage().contains("non-zero balance"));
    }

    @Test
    void closeAccount_failsIfNotActive() {
        AccountAggregate account = new AccountAggregate("ACC-003");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.SUSPENDED);

        Exception ex = assertThrows(IllegalStateException.class, () -> {
            account.execute(new CloseAccountCmd("ACC-003"));
        });

        assertTrue(ex.getMessage().contains("Active"));
    }

    @Test
    void closeAccount_failsIfIdMismatch() {
        AccountAggregate account = new AccountAggregate("ACC-004");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.ACTIVE);

        assertThrows(IllegalArgumentException.class, () -> {
            account.execute(new CloseAccountCmd("DIFFERENT-ID"));
        });
    }
}
