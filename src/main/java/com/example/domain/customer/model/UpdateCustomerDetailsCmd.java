package com.example.domain.customer.model;

import com.example.domain.shared.Command;

/**
 * Command to update contact information or personal details for an existing customer.
 * Includes a flag hasActiveAccounts to allow the aggregate to enforce the invariant
 * that customers with active accounts cannot have their core details modified (or deleted).
 */
public record UpdateCustomerDetailsCmd(
    String customerId,
    String fullName,
    String email,
    String sortCode,
    String dateOfBirth,
    String governmentId,
    boolean hasActiveAccounts
) implements Command {
    
    // Constructor overload for scenarios where active accounts check is not relevant (defaults to false)
    public UpdateCustomerDetailsCmd(String customerId, String fullName, String email, String sortCode, String dateOfBirth, String governmentId) {
        this(customerId, fullName, email, sortCode, dateOfBirth, governmentId, false);
    }
}
