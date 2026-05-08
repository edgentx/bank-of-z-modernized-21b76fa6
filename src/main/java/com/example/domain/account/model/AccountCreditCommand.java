package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

/**
 * Internal helper command for testing setup (funding an account).
 * NOT part of the S-7 public API contract, just a mechanism to violate the 'zero balance' invariant.
 */
public record AccountCreditCommand(String accountNumber, BigDecimal amount) implements Command {}
