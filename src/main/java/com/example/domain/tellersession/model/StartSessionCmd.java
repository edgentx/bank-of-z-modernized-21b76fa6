package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Requires the ID of the authenticated teller and the physical terminal ID.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
    // Validation logic is handled by the aggregate, but records can have compact constructors
    // if basic sanity checks are desired here. We defer to Aggregate for business rules.
}
