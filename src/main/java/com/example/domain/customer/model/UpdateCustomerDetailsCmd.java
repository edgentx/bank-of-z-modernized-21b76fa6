package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record UpdateCustomerDetailsCmd(
    String customerId,
    String emailAddress,
    String sortCode,
    String fullName,
    String dateOfBirth
) implements Command {}
