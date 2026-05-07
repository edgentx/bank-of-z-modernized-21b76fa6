package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Set;

/**
 * Command to initiate a new teller session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Set<String> permissions,
        String navigationState
) implements Command {}
