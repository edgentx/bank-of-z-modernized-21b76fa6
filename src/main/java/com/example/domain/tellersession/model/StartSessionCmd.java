package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTimedOut,
        boolean isNavContextValid
) implements Command {
    
    // Primary constructor for standard valid execution
    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, true, false, true);
    }
}
