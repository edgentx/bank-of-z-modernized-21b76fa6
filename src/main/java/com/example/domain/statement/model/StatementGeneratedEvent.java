package com.example.domain.statement.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event representing the generation of a statement.
 * Used here to set up the state of the aggregate in tests.
 */
public record StatementGeneratedEvent(
    String statementId,
    String accountId,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    boolean isPeriodOpen,
    Instant occurredAt
) {
}
