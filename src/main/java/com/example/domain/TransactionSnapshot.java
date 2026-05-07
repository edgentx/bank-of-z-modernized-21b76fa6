package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Snapshot value object used to reconstruct the Transaction aggregate state.
 */
public record TransactionSnapshot(UUID accountId, BigDecimal balance, boolean isPosted) {}
