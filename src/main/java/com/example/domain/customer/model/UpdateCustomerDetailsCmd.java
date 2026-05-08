package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update personal details for an existing Customer.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String emailAddress,
    String sortCode
) implements Command {}
