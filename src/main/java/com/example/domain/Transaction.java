package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID transactionId;
    private boolean posted = false;
    private boolean allowOverdraft = true;
    private BigDecimal currentBalance = BigDecimal.ZERO;

    public Transaction(UUID transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Execute pattern dispatch.
     * In a larger system, this might use a Visitor or Map<Class<?>, Function<Command, Event>>.
     */
    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.posted) {
            throw new IllegalStateException("Transaction is immutable once posted.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount() == null || cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant: A transaction must result in a valid account balance
        // (Simulating aggregate validation logic here)
        if (!isAllowOverdraft()) {
            BigDecimal projectedBalance = this.currentBalance.subtract(cmd.getAmount());
            if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("A transaction must result in a valid account balance.");
            }
        }

        // Logic: Apply the state change (in reality, this would emit an event applied to the aggregate)
        // For this phase, we return the event directly as requested by the story.
        this.markPosted(); // Side effect of execution for testing state

        return new WithdrawalPostedEvent(
            UUID.randomUUID(),
            cmd.getAccountNumber(),
            cmd.getAmount(),
            cmd.getCurrency()
        );
    }

    public void markPosted() {
        this.posted = true;
    }

    public boolean isPosted() {
        return posted;
    }

    public boolean isAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
}
