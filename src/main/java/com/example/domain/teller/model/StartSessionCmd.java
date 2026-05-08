package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.UUID;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, String navigationState) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }
    }
}