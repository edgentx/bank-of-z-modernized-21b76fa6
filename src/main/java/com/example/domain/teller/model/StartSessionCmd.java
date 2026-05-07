package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Record type used for immutable data transport.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}