package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to close an account.
 * S-7: Account Management
 */
public record CloseAccountCmd(String accountNumber, String currentStatus, java.math.BigDecimal currentBalance, String accountType) implements Command {
}
