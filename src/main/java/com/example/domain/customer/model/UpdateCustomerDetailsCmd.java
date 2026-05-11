package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details such as email or sort code.
 */
public record UpdateCustomerDetailsCmd(String customerId, String fullName, String email, String sortCode) implements Command {}
