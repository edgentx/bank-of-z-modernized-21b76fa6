package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        String initialNavigationState,
        Instant requestTimestamp
) implements Command {}