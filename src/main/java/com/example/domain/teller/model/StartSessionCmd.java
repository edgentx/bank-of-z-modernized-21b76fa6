package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Part of Story S-18.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String authToken // Used for authentication validation
) implements Command {}
