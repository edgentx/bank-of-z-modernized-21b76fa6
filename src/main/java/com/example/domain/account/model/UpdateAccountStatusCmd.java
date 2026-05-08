package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record UpdateAccountStatusCmd(String accountNumber, AccountStatus newStatus) implements Command {
    public UpdateAccountStatusCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber required");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus required");
        }
    }
}
