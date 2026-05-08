package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a customer.
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
