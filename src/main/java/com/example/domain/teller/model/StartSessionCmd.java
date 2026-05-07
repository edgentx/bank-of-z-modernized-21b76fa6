package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    int timeoutMinutes,
    String initialContext
) implements Command {}
