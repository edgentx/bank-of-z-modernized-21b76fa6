package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal being used.
 * @param isAuthenticated Whether the teller has successfully authenticated.
 * @param sessionTimeoutMinutes The configured timeout period for the session.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    int sessionTimeoutMinutes
) implements Command {}
