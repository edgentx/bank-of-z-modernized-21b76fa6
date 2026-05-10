package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Part of Story S-18: TellerSession Aggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
