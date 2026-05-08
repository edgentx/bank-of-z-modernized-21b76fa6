package com.example.domain.customer.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Optional;

/**
 * Command to update contact/personal details for an existing Customer.
 * S-3: Implement UpdateCustomerDetailsCmd on Customer.
 */
public record UpdateCustomerDetailsCmd(
        String customerId,
        String fullName,
        String emailAddress,
        String sortCode,
        String governmentId,
        Instant dateOfBirth,
        boolean hasActiveAccounts
) implements Command {}
