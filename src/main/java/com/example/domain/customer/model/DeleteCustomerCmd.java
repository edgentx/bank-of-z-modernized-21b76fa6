package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 * Requirement: Marks a customer record as deleted if they have no active accounts.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
