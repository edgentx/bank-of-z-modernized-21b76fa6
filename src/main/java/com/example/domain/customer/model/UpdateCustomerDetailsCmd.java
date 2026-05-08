package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer contact/personal details.
 * S-3: Implement UpdateCustomerDetailsCmd on Customer.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode,
        String fullName,     // Optional: allows validating "cannot be empty" scenarios
        String dateOfBirth   // Optional: allows validating DOB scenarios
) implements Command {}
