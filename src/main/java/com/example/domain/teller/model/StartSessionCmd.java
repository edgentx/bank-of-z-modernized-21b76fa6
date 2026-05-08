package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * S-18: user-interface-navigation.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        String navigationContext
) implements Command {}
