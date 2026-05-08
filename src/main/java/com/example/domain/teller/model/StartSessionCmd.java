package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isNavigationContextValid) implements Command {
    
    public StartSessionCmd {
        // Defaults for convenience in tests, ensuring validity unless explicitly set otherwise
        if (isNavigationContextValid) { /* Explicitly handled by builder/usage */ }
    }
}
