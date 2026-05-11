package com.example.domain.customer.model;

import com.example.domain.shared.Command;

import java.time.LocalDate;

/**
 * Command to update customer details.
 * Invariants enforced in the aggregate:
 * - Name and DOB cannot be empty.
 * - Email must be valid format.
 * - Government ID is required.
 * - Customer cannot be soft-deleted if they have active accounts (via hasActiveAccounts flag).
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        LocalDate dateOfBirth,
        String governmentId,
        boolean hasActiveAccounts,
        String sortCode
) implements Command {}
