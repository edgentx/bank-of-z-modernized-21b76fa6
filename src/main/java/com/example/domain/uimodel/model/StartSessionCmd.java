package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller session.
 * Invariant enforcement requires valid Teller and Terminal IDs.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
