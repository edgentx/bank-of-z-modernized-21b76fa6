package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.util.Set;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Duration inactivityTimeout,
        String navigationState
) implements Command {}
