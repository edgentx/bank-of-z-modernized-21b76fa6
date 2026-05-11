package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record OpenAccountCmd(
    String accountId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode,
    String providedAccountNumber // Optional: If null, aggregate generates it. If present, must be unique.
) implements Command {}
