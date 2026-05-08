package com.example.domain.customer.model;

import com.example.domain.shared.Command;

import java.time.LocalDate;

public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String emailAddress,
    String sortCode,
    String governmentId,
    LocalDate dateOfBirth,
    boolean hasActiveAccounts
) implements Command {}
