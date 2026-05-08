package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact information or personal details for an existing customer.
 * Used in S-3: UpdateCustomerDetailsCmd.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode,
        String fullName,
        String dateOfBirth,
        boolean active,
        boolean hasActiveAccounts
) implements Command {}
