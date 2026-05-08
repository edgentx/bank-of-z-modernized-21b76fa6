package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Contains flags to simulate domain state violations for testing invariants.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated,
        boolean terminalContextValid,
        boolean sessionStale
) implements Command {}
