package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
