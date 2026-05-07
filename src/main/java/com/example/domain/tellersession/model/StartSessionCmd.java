package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Used in S-18: StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
}
