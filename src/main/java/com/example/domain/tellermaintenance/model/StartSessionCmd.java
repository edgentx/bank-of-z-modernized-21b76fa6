package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 * ID: S-18
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
