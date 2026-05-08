package com.example.domain.customer.model;

import com.example.domain.shared.Command;

import java.util.List;

/**
 * Command to delete a customer.
 * Precondition: The customer must have valid details (email, government ID, name, DOB) and no active accounts.
 */
public record DeleteCustomerCmd(
        String customerId,
        String governmentId,
        String dateOfBirth,
        List<String> activeAccountIds
) implements Command {}
