package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String fullName, // Optional for update
    boolean requestingDeletion // Used to test the "active accounts" invariant scenario
) implements Command {}
