package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact and personal details for an existing Customer.
 * Satisfies S-3 Story requirements.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String emailAddress,
        String fullName,
        String sortCode
) implements Command {}
