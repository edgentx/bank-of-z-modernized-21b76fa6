package com.example.domain.tellercmd.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
