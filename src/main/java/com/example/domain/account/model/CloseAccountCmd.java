package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to close an account.
 */
public record CloseAccountCmd(String accountId) implements Command {
    public CloseAccountCmd {
        Objects.requireNonNull(accountId, "accountId cannot be null");
        if (accountId.isBlank()) throw new IllegalArgumentException("accountId cannot be blank");
    }
}