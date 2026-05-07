package com.example.domain.tellermatching.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String authToken,
    Instant loginTime,
    Duration sessionTimeout
) implements Command {}
