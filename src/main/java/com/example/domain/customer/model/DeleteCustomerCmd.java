package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Records the intent to mark a customer as deleted.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
