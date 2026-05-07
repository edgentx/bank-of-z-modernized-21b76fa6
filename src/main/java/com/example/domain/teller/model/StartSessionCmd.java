package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}