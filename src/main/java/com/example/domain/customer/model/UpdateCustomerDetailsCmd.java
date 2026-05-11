package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact details for a Customer.
 * Includes validation flags (hasActiveAccounts) to enforce domain invariants statelessly.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        String governmentId,
        String dateOfBirth,
        String sortCode,
        boolean hasActiveAccounts
) implements Command {}
