package com.example.domain.transaction;

import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionAggregateTest {

    // 
    // Scenario: Successfully execute PostWithdrawalCmd
    // 
    @Test
    void shouldEmitWithdrawalPostedEventWhenCommandIsValid() {
        // Given
        String transactionId = "TX-123";
        String accountNumber = "ACC-456";
        BigDecimal openingBalance = new BigDecimal("100.00");
        String currency = "USD";
        TransactionAggregate aggregate = new TransactionAggregate(transactionId, accountNumber, openingBalance, currency);
        
        BigDecimal withdrawalAmount = new BigDecimal("50.00");
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(accountNumber, withdrawalAmount, currency);

        // When
        List events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "An event should be emitted");
        assertTrue(events.get(0) instanceof WithdrawalPostedEvent, "A WithdrawalPostedEvent should be emitted");
        
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) events.get(0);
        assertEquals("withdrawal.posted", event.type());
        assertEquals(transactionId, event.aggregateId());
        assertEquals(withdrawalAmount, event.amount());
        assertEquals(accountNumber, event.accountNumber());
        
        assertEquals(new BigDecimal("50.00"), aggregate.getCurrentBalance());
        assertTrue(aggregate.isPosted());
    }

    // 
    // Scenario: PostWithdrawalCmd rejected — Transaction amounts must be greater than zero.
    // 
    @Test
    void shouldRejectCommandWhenAmountIsZero() {
        // Given
        TransactionAggregate aggregate = new TransactionAggregate("TX-1", "ACC-1", BigDecimal.TEN, "USD");
        PostWithdrawalCmd cmd = new PostWithdrawalCmd("ACC-1", BigDecimal.ZERO, "USD");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("greater than zero"));
    }

    @Test
    void shouldRejectCommandWhenAmountIsNegative() {
        // Given
        TransactionAggregate aggregate = new TransactionAggregate("TX-1", "ACC-1", BigDecimal.TEN, "USD");
        PostWithdrawalCmd cmd = new PostWithdrawalCmd("ACC-1", new BigDecimal("-10.00"), "USD");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("greater than zero"));
    }

    // 
    // Scenario: PostWithdrawalCmd rejected — Transactions cannot be altered or deleted once posted
    // 
    @Test
    void shouldRejectCommandWhenTransactionAlreadyPosted() {
        // Given
        TransactionAggregate aggregate = new TransactionAggregate("TX-1", "ACC-1", BigDecimal.TEN, "USD");
        
        // First execution succeeds
        PostWithdrawalCmd firstCmd = new PostWithdrawalCmd("ACC-1", BigDecimal.ONE, "USD");
        aggregate.execute(firstCmd);
        assertTrue(aggregate.isPosted());

        // Attempt to execute again (alter)
        PostWithdrawalCmd secondCmd = new PostWithdrawalCmd("ACC-1", BigDecimal.ONE, "USD");

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(secondCmd);
        });

        assertTrue(exception.getMessage().contains("cannot be altered"));
    }

    // 
    // Scenario: PostWithdrawalCmd rejected — A transaction must result in a valid account balance
    // 
    @Test
    void shouldRejectCommandWhenInsufficientFunds() {
        // Given
        TransactionAggregate aggregate = new TransactionAggregate("TX-1", "ACC-1", new BigDecimal("5.00"), "USD");
        PostWithdrawalCmd cmd = new PostWithdrawalCmd("ACC-1", new BigDecimal("10.00"), "USD");

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("valid account balance") || exception.getMessage().contains("Insufficient"));
    }
}