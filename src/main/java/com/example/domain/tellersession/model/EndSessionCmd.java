package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record EndSessionCmd(String sessionId) implements Command {
    if (sessionId == null || sessionId.isBlank()) {
        throw new IllegalArgumentException("sessionId cannot be null or blank");
    }
}