package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record CompleteTransferCmd(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    BigDecimal availableBalance,
    boolean atomicStateValid
) implements Command {}
