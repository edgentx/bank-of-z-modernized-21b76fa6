package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * The invariant check regarding active accounts is typically performed by the Aggregate 
 * based on its internal state, but can be passed via command if fetched externally.
 * Here we include it to facilitate the domain logic without an external dependency lookup in the aggregate.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
