package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to close an account.
 */
public record CloseAccountCmd(String accountId, String accountNumber) implements Command {
    public CloseAccountCmd {
        Objects.requireNonNull(accountId, "accountId cannot be null");
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
    }
}
