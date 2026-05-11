package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update details for an existing Customer.
 * Includes optional fields: email, fullName, dateOfBirth.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String fullName,
    String dateOfBirth
) implements Command {}
