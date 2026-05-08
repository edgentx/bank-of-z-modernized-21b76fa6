package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Business invariant: Customer must not have active accounts.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
