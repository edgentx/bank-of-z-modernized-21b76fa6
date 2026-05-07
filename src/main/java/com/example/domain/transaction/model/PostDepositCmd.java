package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;
import java.util.UUID;

public record PostDepositCmd(String transactionId, String accountNumber, BigDecimal amount, String currency) implements Command {
    public PostDepositCmd {
        if (transactionId == null) throw new IllegalArgumentException("transactionId required");
        if (accountNumber == null) throw new IllegalArgumentException("accountNumber required");
        if (amount == null) throw new IllegalArgumentException("amount required");
        if (currency == null) throw new IllegalArgumentException("currency required");
    }
}
