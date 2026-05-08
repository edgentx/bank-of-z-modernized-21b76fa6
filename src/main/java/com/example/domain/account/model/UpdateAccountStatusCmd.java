package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record UpdateAccountStatusCmd(
    String accountNumber,
    AccountAggregate.AccountStatus newStatus,
    AccountAggregate.AccountType type,
    BigDecimal balance
) implements Command {}
