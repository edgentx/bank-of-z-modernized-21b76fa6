package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update details for an existing Customer.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String governmentId,
    String fullName,
    String dateOfBirth,
    boolean hasActiveAccounts
) implements Command {}
