package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: S-18 TellerSession user-interface-navigation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    // Validation is performed by the aggregate, but constructor checks can be added if strictness is needed.
}