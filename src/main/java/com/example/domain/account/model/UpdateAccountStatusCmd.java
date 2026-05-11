package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record UpdateAccountStatusCmd(
        String targetAccountNumber,
        AccountAggregate.AccountStatus newStatus
) implements Command {
    public UpdateAccountStatusCmd {
        Objects.requireNonNull(targetAccountNumber);
        Objects.requireNonNull(newStatus);
    }
}
