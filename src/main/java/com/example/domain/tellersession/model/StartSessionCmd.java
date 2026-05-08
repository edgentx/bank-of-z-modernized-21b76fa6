package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String navigationState // E.g., "HOME", "LOCKED", or "TRANSITIONAL"
) implements Command {}
