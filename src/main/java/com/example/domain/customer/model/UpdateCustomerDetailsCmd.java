package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update personal details for an existing customer.
 * Accepts updated name, email, date of birth, and a new sort code.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String email,
    String dateOfBirth,
    String sortCode
) implements Command {}
