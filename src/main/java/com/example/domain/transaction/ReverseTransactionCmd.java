package com.example.domain.transaction;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to reverse a specific transaction.
 * The amount provided is the expected amount of the transaction to be reversed.
 */
public record ReverseTransactionCmd(String transactionId, BigDecimal amount) implements Command {
    public ReverseTransactionCmd {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("transactionId cannot be null");
        }
        // Amount validation happens in the Aggregate, but we can do basic checks here if desired.
    }
}