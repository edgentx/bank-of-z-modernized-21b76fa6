package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record OpenAccountCmd(
    String accountId,
    String customerId,
    String accountType,
    String sortCode,
    BigDecimal initialDeposit
) implements Command {}
