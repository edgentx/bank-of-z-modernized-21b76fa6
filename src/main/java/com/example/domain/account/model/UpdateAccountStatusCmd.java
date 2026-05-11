package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record UpdateAccountStatusCmd(String accountNumber, AccountAggregate.AccountStatus newStatus) implements Command {
    public UpdateAccountStatusCmd {
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(newStatus, "newStatus required");
    }
}
