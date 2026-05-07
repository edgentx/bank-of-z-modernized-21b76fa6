package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record InitiateTellerSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public InitiateTellerSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}
