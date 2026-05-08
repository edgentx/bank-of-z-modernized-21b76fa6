package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a Customer aggregate.
 * Usage: new DeleteCustomerCmd(customerId)
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
