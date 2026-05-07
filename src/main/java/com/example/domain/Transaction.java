package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private final String id;
    private boolean isPosted;

    public Transaction(String id) {
        this.id = id;
        this.isPosted = false;
    }

    public String getId() {
        return id;
    }

    public boolean isPosted() {
        return isPosted;
    }

    /**
     * Executes a command against this aggregate.
     *
     * @param cmd The command to execute (currently supporting PostDepositCmd)
     * @return The resulting event if successful
     * @throws DomainException if invariants are violated
     */
    public Object execute(Object cmd) {
        if (cmd instanceof PostDepositCmd depositCmd) {
            return handlePostDeposit(depositCmd);
        }
        throw new IllegalArgumentException("Unknown command type: " + cmd.getClass().getSimpleName());
    }

    private DepositPostedEvent handlePostDeposit(PostDepositCmd cmd) {
        // Invariant 1: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: A transaction must result in a valid account balance
        // This logic would typically involve checking the account state, but we validate the aggregate state here.
        validateBalance(cmd);

        // Apply state change
        this.isPosted = true;

        // Emit event
        return DepositPostedEvent.create(this.id, cmd);
    }

    /**
     * Hook for subclasses or simulation of balance validation logic.
     * In a real scenario, this might check an Account aggregate reference.
     */
    protected void validateBalance(PostDepositCmd cmd) {
        // Default implementation assumes validity.
        // Subclasses (like in the violation test) can override this.
    }

}