package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer contact/personal details.
 * S-3: Implement UpdateCustomerDetailsCmd.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String emailAddress,
    String sortCode
) implements Command {}
