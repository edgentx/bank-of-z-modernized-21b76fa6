package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Includes flags to simulate domain violations for testing purposes.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,   // Represents auth status
    boolean timedOut,        // Represents inactivity
    boolean navigationValid  // Represents context validity
) implements Command {}
