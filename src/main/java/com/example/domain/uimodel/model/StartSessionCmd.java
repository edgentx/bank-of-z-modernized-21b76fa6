package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Part of S-18 implementation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
