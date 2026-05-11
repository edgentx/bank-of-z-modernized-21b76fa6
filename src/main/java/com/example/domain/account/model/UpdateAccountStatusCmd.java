package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

/**
 * Command to update the status of an Account.
 */
public record UpdateAccountStatusCmd(
    String accountNumber,
    AccountAggregate.AccountStatus newStatus,
    BigDecimal contextBalance // Used to pass balance context for invariant checking in test
) implements Command {}
