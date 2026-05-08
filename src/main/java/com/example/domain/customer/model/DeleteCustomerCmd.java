package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to delete a Customer aggregate.
 * Business rule: Customer can only be deleted if they have no active accounts 
 * and valid profile data (enforced in the aggregate).
 */
public record DeleteCustomerCmd(String customerId) implements Command {}
