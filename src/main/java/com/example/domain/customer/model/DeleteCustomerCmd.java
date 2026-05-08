package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Invariant check (active accounts) is handled by the aggregate.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
