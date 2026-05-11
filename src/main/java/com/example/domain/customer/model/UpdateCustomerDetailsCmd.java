package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update a customer's details.
 * Includes validation flags populated by the application service layer.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String email,
        String sortCode,
        boolean isEmailUnique,
        boolean hasActiveAccounts
) implements Command {}