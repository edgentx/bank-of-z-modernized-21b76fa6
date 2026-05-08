package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initialize a teller session.
 * Needed to establish the 'authenticated' state required by S-19 scenarios.
 */
public record InitiateSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
