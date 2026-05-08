package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Optional;

/**
 * Command to update the status of an Account aggregate.
 */
public record UpdateAccountStatusCmd(
    String accountId,
    AccountAggregate.AccountStatus newStatus,
    String accountNumber // Optional inclusion to check immutability invariant
) implements Command {}
