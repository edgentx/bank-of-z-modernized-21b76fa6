package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant authenticatedAt
) implements Command {}