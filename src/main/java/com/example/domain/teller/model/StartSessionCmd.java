package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartSessionCmd(String tellerId, String terminalId, Instant authenticatedAt) implements Command {
    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be null");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be null");
        if (authenticatedAt == null) throw new IllegalArgumentException("authenticatedAt cannot be null");
    }
}
