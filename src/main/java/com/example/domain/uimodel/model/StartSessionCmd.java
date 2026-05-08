package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
