package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    boolean isTimedOut,
    String navigationState
) implements Command {}
