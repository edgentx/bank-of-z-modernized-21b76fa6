package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details such as email address and sort code.
 * Includes flags to allow the Aggregate to verify invariants regarding active accounts
 * and uniqueness enforcement without direct dependencies on other bounded contexts.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode,
        boolean hasActiveAccounts,
        boolean isEmailUnique
) implements Command {}
