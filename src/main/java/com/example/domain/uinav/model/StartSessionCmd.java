package com.example.domain.uinav.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * AC: Requires valid tellerId and terminalId.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    
    public enum State {
        AUTHENTICATED,
        UNAUTHENTICATED
    }
}