package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Checks invariants:
 * 1. Must have valid identity (email, govId).
 * 2. Must have valid demographics (name, dob).
 * 3. Must not have active accounts.
 */
public record DeleteCustomerCmd(String customerId, boolean hasActiveAccounts) implements Command {}
