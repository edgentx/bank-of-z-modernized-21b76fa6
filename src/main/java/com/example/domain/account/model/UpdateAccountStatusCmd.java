package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to update the status of an Account.
 * Story: S-6.
 */
public record UpdateAccountStatusCmd(String accountNumber, String newStatus) implements Command {
    // Validation
    public UpdateAccountStatusCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber required");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus required");
        }
    }
}
