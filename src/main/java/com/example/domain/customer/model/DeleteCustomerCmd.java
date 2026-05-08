package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record DeleteCustomerCmd(String customerId, boolean hasActiveAccounts) implements Command {
}