package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Duration;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Duration timeout,
    String initialContext
) implements Command {}
