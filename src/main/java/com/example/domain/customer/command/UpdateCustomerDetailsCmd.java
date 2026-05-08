package com.example.domain.customer.command;

import com.example.domain.shared.Command;

/**
 * Command to update contact details (email, sort code) for a Customer.
 */
public record UpdateCustomerDetailsCmd(String customerId, String emailAddress, String sortCode) implements Command {
}
