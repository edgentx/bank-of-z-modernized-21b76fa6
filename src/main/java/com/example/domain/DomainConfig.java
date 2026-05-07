package com.example.domain;

import java.math.BigDecimal;

/**
 * Configuration object to enforce domain invariants.
 */
public record DomainConfig(BigDecimal maxTransactionAmount, BigDecimal maxAccountBalance) {}
