package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to complete a transfer.
 */
public record CompleteTransferCmd(
    String transferReference,
    String sourceAccount,
    String destinationAccount,
    BigDecimal amount,
    String currency
) implements Command {}
