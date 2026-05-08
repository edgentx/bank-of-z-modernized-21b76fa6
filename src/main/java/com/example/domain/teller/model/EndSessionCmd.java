package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}