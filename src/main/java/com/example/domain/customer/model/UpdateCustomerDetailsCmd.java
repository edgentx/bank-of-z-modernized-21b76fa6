package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        String sortCode
) implements Command {}
