package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * @param tellerId The authenticated user ID.
 * @param terminalId The physical or virtual terminal identifier.
 * @param isAuthenticated The authentication status flag (must be true).
 * @param isActive Whether the session is currently active (for timeout checks).
 * @param lastActivityTimestamp The timestamp of the last activity (for timeout checks).
 * @param currentContext The current navigation context (must be valid).
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isActive,
    long lastActivityTimestamp,
    String currentContext
) implements Command {}
