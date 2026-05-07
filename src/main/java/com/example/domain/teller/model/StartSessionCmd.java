package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * @param sessionId The unique identifier for the session aggregate.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal where the session is starting.
 * @param isAuthenticated Flag indicating if the teller has passed authentication checks.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated
) implements Command {}
