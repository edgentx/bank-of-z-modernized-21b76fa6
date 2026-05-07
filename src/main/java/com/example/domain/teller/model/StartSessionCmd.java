package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 * 
 * @param sessionId The unique identifier for the session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal being used.
 * @param authenticated Flag indicating if the teller has successfully authenticated.
 * @param validTimeoutConfig Flag indicating if the timeout configuration is valid.
 * @param validNavigationContext Flag indicating if the navigation context is valid.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    boolean validTimeoutConfig,
    boolean validNavigationContext
) implements Command {}
