package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String aggregateId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String functionKey, // Represents navigation context/state
    int timeoutMinutes,
    int screenId
) implements Command {}
