package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final Currency currency;
    private final TransactionStatus status;

    public Transaction(UUID id, String accountNumber, BigDecimal currentBalance, Currency currency, TransactionStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.currency = currency;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Executes a command against this aggregate instance.
     * Invariants are enforced here.
     *
     * @param command The command to execute.
     * @return The resulting event if successful.
     * @throws DomainException if invariants are violated.
     */
    public TransactionEvent execute(PostWithdrawalCmd command) {
        // Invariant: Transaction cannot be altered once posted.
        if (this.status == TransactionStatus.POSTED) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Amount must be greater than zero.
        if (command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Valid account balance (prevent overdraft).
        // Assuming a hard floor at 0.00 for this aggregate logic.
        BigDecimal potentialBalance = this.currentBalance.subtract(command.getAmount());
        if (potentialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // If invariants passed, emit the event.
        return new WithdrawalPostedEvent(
                this.id,
                this.accountNumber,
                command.getAmount(),
                command.getCurrency()
        );
    }

    // Add generic execute dispatch if other commands are supported later
    public TransactionEvent execute(Object command) {
        if (command instanceof PostWithdrawalCmd) {
            return execute((PostWithdrawalCmd) command);
        }
        throw new UnsupportedOperationException("Unknown command type: " + command.getClass().getSimpleName());
    }
}
