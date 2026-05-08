package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Validated by TellerSession Aggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}