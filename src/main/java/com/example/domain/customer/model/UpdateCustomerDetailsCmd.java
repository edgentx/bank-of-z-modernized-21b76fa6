package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact information or personal details for an existing customer.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        String sortCode
) implements Command {
}