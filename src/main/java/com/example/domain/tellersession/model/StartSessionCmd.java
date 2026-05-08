package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * @param tellerId The authenticated teller's ID
 * @param terminalId The physical terminal ID where the session starts
 * @param isAuthenticated Whether the teller has successfully authenticated
 * @param isTimeoutConfigured Whether the inactivity timeout is properly configured
 * @param isNavigationStateValid Whether the initial navigation state is valid
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimeoutConfigured,
    boolean isNavigationStateValid
) implements Command {}
