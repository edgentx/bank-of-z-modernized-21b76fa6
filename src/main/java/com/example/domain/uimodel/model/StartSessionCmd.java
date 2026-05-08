package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * Part of Story S-18: TellerSession (user-interface-navigation).
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        // Basic validation checks can be enforced at the command boundary if desired,
        // or handled by the aggregate. We rely on the aggregate for full validation.
    }
}
