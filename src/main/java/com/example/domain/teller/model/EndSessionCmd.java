package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId required");
        }
    }
}