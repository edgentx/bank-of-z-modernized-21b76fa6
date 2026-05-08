package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Records the intent to start a session with specific UI context.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    int timeoutInSeconds,
    String navigationContext
) implements Command {}
