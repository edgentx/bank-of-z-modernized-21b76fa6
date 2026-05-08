package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to open a new bank account.
 * S-5: Implement OpenAccountCmd on Account.
 */
public record OpenAccountCmd(
        String customerId,
        String accountType,
        BigDecimal initialDeposit,
        String sortCode,
        String accountNumber
) implements Command {
    public OpenAccountCmd {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId cannot be null or blank");
        }
        if (accountType == null || accountType.isBlank()) {
            throw new IllegalArgumentException("accountType cannot be null or blank");
        }
        if (initialDeposit == null) {
            throw new IllegalArgumentException("initialDeposit cannot be null");
        }
        if (sortCode == null || sortCode.isBlank()) {
            throw new IllegalArgumentException("sortCode cannot be null or blank");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber cannot be null or blank");
        }
    }
}
