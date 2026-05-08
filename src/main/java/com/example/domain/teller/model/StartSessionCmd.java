package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant lastActivityAt,
        String navigationContext
) implements Command {}
