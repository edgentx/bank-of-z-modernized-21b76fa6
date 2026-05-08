package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Includes a boolean flag indicating if the customer has active accounts.
 * This flag is populated by the application service layer after querying the Account store,
 * allowing the Aggregate to enforce the invariant statelessly or semi-statelessly.
 */
public record DeleteCustomerCmd(String customerId, boolean hasActiveAccounts) implements Command {}
