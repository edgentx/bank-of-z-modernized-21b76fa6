package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    long lastActivityAt,
    String initialState
) implements Command {}
