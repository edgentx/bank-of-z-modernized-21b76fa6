package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * S-18: Command to initiate a Teller Session.
 * Validations: authenticated Teller, active Terminal, valid timeout config.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isTerminalActive,
        long timeoutMillis,
        String contextNavState
) implements Command {
}
