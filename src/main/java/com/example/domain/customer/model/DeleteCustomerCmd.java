package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Contract: The aggregate must verify the customer is valid and has no active accounts.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
