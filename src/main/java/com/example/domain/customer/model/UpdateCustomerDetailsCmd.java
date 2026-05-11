package com.example.domain.customer.model;

import com.example.domain.shared.Command;

import java.time.LocalDate;

public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String email,
        LocalDate dateOfBirth,
        String sortCode
) implements Command {}
