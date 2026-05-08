package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode,
        String fullName,
        String dateOfBirth,
        String governmentId,
        boolean hasActiveBankAccounts // Flag to simulate checking active accounts for the scenario
) implements Command {}
