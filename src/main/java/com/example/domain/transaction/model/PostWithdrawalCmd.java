package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record PostWithdrawalCmd(
    String transactionId,
    String accountNumber,
    BigDecimal amount,
    String currency,
    BigDecimal currentBalance
) implements Command {}
