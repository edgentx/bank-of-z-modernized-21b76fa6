package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record ReverseTransactionCmd(
        String transactionId,
        String originalTransactionId,
        BigDecimal amount
) implements Command {}
