package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to initiate a Teller Session.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentNavigationState,
    UUID sessionId
) implements Command {
}
