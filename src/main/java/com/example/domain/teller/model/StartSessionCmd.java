package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Duration;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Duration timeout,
    Instant occurredAt
) implements Command {}
