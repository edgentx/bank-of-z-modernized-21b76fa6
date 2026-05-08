package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant sessionTimeoutAt, // Derived from config in a real handler, used here for validation testing
        String navigationContext   // Used to test navigation state validity
) implements Command {}
