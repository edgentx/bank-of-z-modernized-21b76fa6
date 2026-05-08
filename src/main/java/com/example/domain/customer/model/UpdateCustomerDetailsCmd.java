package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String emailAddress,
    String governmentId,
    String sortCode
) implements Command {}