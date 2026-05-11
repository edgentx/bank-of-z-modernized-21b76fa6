package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details such as contact info and sort code.
 * Includes a boolean flag indicating if the customer has active accounts,
 * required to enforce the invariant: A customer cannot be deleted if they own active bank accounts.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        String governmentId,
        String dateOfBirth,
        String sortCode,
        boolean hasActiveAccounts
) implements Command {
}