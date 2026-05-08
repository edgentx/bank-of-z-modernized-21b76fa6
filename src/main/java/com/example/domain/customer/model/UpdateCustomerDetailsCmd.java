package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update personal details for an existing customer.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode
) implements Command {}
