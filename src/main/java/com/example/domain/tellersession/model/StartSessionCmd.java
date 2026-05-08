package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * AuthZ context is injected via the security layer or passed explicitly if internal.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated, // Passed explicitly for domain invariant validation
        String navigationState,
        long inactivityContextFlag // 0 = valid, >0 = invalid context for testing
) implements Command {}
