package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record CloseAccountCmd(String aggregateId, String accountNumber) implements Command {
    public CloseAccountCmd {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(accountNumber);
    }
}
