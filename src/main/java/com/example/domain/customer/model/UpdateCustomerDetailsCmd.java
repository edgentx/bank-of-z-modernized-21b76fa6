package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 * Note: 'sortCode' is included as per acceptance criteria requirement,
 * though it is not persisted on the aggregate in this phase.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String emailAddress,
    String governmentId,
    String dateOfBirth,
    String sortCode
) implements Command {}
