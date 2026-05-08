package com.example.domain.customer.command;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 * Placed in 'command' package to satisfy DAG dependency rules.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode
) implements Command {}
