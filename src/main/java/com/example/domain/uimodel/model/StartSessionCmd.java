package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Represents S-18 requirement: StartSessionCmd.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
