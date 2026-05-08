package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to update the status of an Account.
 */
public record UpdateAccountStatusCmd(
        String aggregateId,
        String accountNumber,
        AccountAggregate.AccountStatus newStatus
) implements Command {

    public UpdateAccountStatusCmd {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(accountNumber, "accountNumber required");
        Objects.requireNonNull(newStatus, "newStatus required");
    }
}
