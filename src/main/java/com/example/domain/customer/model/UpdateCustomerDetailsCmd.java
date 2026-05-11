package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 * Includes fields for email, name, and sort code.
 * Invariants are enforced by the CustomerAggregate.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String email,
    String sortCode
) implements Command {}
