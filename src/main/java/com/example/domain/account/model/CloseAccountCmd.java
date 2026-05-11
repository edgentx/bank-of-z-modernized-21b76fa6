package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record CloseAccountCmd(String accountId, String accountNumber) implements Command {
    public CloseAccountCmd {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(accountNumber);
    }
}
