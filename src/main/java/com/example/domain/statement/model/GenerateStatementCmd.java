package com.example.domain.statement.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GenerateStatementCmd(
    String statementId,
    String accountNumber,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    LocalDate periodEnd
) implements Command {}
