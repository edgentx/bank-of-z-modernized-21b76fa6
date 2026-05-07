package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Command to post a deposit transaction.
 * S-10: Implement PostDepositCmd on Transaction
 */
public record PostDepositCmd(String transactionId, String accountId, BigDecimal amount, String currency) implements Command {
}
