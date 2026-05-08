package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact information for an existing customer.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String governmentId,
    String fullName,
    String dateOfBirth
) implements Command {}
