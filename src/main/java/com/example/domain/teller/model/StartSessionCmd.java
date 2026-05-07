package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    String navigationContext,
    Instant timestamp
) implements Command {}
