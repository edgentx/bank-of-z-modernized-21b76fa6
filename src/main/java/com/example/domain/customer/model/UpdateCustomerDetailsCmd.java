package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update customer details.
 * Only fields present in the constructor are considered for update.
 */
public record UpdateCustomerDetailsCmd(
  String customerId,
  String fullName,
  String email,
  String sortCode,
  String governmentId,
  String dateOfBirth,
  boolean hasActiveBankAccounts
) implements Command {}
