package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active Teller Session.
 * Validates that the session is in a valid state to be terminated.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    // Validation of the record fields can be done via compact constructors if strict
    // pre-condition checks are needed before the aggregate even sees it,
    // but typically the Aggregate holds the business rules.
}
