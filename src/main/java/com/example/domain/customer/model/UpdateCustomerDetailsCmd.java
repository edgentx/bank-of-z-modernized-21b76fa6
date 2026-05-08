package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update personal details for an existing customer.
 * Corresponds to Story S-3.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String fullName,
    String dateOfBirth
) implements Command {}
