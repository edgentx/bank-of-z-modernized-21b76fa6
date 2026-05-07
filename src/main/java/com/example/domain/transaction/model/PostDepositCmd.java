package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record PostDepositCmd(String accountNumber, BigDecimal amount, String currency) implements Command {
    // Constructor validation can be added here if needed, 
    // though aggregate validation is the primary source of truth.
}