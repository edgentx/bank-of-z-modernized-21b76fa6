package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to update the status of an Account.
 */
public record UpdateAccountStatusCmd(String accountNumber, AccountAggregate.AccountStatus newStatus, String violationConstraint) implements Command {
    
    public UpdateAccountStatusCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus required");
        }
    }

    // Convenience constructor for happy path
    public UpdateAccountStatusCmd(String accountNumber, AccountAggregate.AccountStatus newStatus) {
        this(accountNumber, newStatus, null);
    }
}