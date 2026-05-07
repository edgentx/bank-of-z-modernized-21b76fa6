package com.example.domain.transaction;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionAggregateTest {

    @Test
    void shouldPostWithdrawalSuccessfully() {
        String txnId = "txn-1";
        String acctId = "acct-1";
        TransactionAggregate aggregate = new TransactionAggregate(txnId);
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(txnId, acctId, new BigDecimal("100.00"), "USD");

        List<DomainEvent> events = aggregate.execute(cmd);

        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof WithdrawalPostedEvent);
        
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) events.get(0);
        assertEquals("withdrawal.posted", event.type());
        assertEquals(txnId, event.aggregateId());
        assertEquals(new BigDecimal("100.00"), event.amount());
        assertTrue(aggregate.isPosted());
    }

    @Test
    void shouldRejectAmountZeroOrLess() {
        String txnId = "txn-2";
        TransactionAggregate aggregate = new TransactionAggregate(txnId);
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(txnId, "acct-1", BigDecimal.ZERO, "USD");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("greater than zero"));
    }

    @Test
    void shouldRejectIfAlreadyPosted() {
        String txnId = "txn-3";
        TransactionAggregate aggregate = new TransactionAggregate(txnId);
        
        // Post once
        PostWithdrawalCmd cmd1 = new PostWithdrawalCmd(txnId, "acct-1", new BigDecimal("10.00"), "USD");
        aggregate.execute(cmd1);

        // Try to post again (or modify)
        // We instantiate a new command, but the aggregate state is already posted.
        // Note: In a real CQRS scenario, we might load the aggregate, then execute a new command.
        // Here, execute() on a posted aggregate should fail.
        Exception ex = assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd1));
        assertTrue(ex.getMessage().contains("already posted"));
    }

    @Test
    void shouldRejectInvalidBalanceAmount() {
        String txnId = "txn-4";
        TransactionAggregate aggregate = new TransactionAggregate(txnId);
        // Exceeds internal limit
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(txnId, "acct-1", new BigDecimal("9999999999"), "USD");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("balance limits"));
    }
}
