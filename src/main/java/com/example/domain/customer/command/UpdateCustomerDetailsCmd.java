package com.example.domain.customer.command;

import com.example.domain.shared.Command;

/**
 * Command to update contact information or personal details for an existing customer.
 * Located in the 'command' package to adhere to DDD structure and avoid duplication in 'model'.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String fullName,
    String dateOfBirth
) implements Command {}
