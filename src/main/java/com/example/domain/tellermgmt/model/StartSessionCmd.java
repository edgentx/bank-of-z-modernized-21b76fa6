package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Used in S-18.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
