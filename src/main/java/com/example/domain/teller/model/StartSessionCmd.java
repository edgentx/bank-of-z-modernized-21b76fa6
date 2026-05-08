package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 * Used to setup the aggregate state for testing EndSessionCmd.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant startedAt) implements Command {}
