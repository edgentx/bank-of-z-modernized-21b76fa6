package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    String periodEnd,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    boolean isClosedPeriod
) implements Command {}
