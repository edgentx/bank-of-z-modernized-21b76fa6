package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * 
 * @param sessionId The unique ID of the session to start.
 * @param tellerId  The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal where the session is initiated.
 * @param isAuthenticated Whether the teller has successfully authenticated.
 * @param isActive Whether the terminal context is currently active.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isActive
) implements Command {}
