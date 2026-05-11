package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to update the status of an Account.
 * S-6: Implement UpdateAccountStatusCmd.
 */
public record UpdateAccountStatusCmd(String accountNumber, AccountStatus newStatus) implements Command {
    public UpdateAccountStatusCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber cannot be null or blank");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus cannot be null");
        }
    }
}
