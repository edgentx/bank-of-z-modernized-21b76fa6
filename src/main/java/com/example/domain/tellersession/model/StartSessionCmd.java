package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Validated by the TellerSession aggregate before emitting SessionStartedEvent.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    String navigationState
) implements Command {}
