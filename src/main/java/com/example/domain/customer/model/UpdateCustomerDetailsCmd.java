package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update an existing customer's contact details.
 * Carries the new emailAddress and sortCode to apply to the Customer aggregate.
 */
public record UpdateCustomerDetailsCmd(String customerId, String emailAddress, String sortCode) implements Command {}
