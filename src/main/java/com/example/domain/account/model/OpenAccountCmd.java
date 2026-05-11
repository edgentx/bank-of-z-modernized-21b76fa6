package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Internal helper command used to set up Aggregate state in tests.
 * In a real scenario, this would be the primary entry point.
 */
public record OpenAccountCmd(String accountId, String accountNumber, BigDecimal balance, String status) implements Command {}
