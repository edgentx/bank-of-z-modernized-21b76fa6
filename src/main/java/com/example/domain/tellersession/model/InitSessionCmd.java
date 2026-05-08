package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

// Helper command for test setup to emulate a valid session state
public record InitSessionCmd(String sessionId, String tellerId, Instant initiatedAt) implements Command {}
