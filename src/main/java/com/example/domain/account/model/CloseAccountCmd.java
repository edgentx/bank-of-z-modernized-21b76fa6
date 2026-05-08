package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record CloseAccountCmd(String accountNumber) implements Command {
    public CloseAccountCmd {
        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        if (accountNumber.isBlank()) throw new IllegalArgumentException("accountNumber cannot be blank");
    }
}
