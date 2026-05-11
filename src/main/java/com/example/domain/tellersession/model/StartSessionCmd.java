package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * This command is issued after successful authentication.
 */
public record StartSessionCmd(
    String aggregateId,
    String tellerId,
    String terminalId,
    boolean isTimedOut, // Flag to simulate invariant violation in testing
    boolean isNavInvalid // Flag to simulate invariant violation in testing
) implements Command {}
