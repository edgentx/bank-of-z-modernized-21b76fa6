package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
