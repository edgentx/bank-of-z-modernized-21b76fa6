package com.example.domain.customer.model;

import com.example.domain.shared.Command;

public record UpdateCustomerDetailsCmd(
    String customerId,
    String email,
    String fullName,
    String sortCode,
    String governmentId,
    boolean markDeleted
) implements Command {}
