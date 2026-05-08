package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record UpdateAccountStatusCmd(String accountNumber, String newStatus) implements Command {
    public UpdateAccountStatusCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber cannot be null or blank");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus cannot be null or blank");
        }
    }
}