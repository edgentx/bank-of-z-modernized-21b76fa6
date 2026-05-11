package com.example.domain.customer.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record UpdateCustomerDetailsCmd(
        String customerId,
        String newFullName,
        String newEmailAddress,
        String newSortCode,
        String governmentId,
        Instant dateOfBirth,
        boolean hasActiveAccounts // Used to enforce the 'active accounts' invariant
) implements Command {}
