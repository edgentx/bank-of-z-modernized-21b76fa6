package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact information for an existing customer.
 * Sort code is included as per scenario requirements.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String sortCode
) implements Command {
}
