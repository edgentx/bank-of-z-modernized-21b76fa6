package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
    }
}
