package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record CompleteTransferCmd(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency
) implements Command {}
