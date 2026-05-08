package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Enforces invariants regarding active accounts before deletion.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
