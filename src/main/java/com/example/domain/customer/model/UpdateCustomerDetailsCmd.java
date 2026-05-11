package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer contact details.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String email,
        String sortCode
) implements Command {}
