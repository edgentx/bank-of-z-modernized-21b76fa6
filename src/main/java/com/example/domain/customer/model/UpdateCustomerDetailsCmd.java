package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update personal or contact details for an existing Customer.
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String fullName,
    String dateOfBirth
) implements Command {
}