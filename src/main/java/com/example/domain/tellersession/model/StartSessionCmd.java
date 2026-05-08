package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        String navigationState,
        Instant occurredAt
) implements Command {}
