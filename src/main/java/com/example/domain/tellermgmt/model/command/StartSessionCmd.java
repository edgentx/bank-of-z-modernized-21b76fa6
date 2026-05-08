package com.example.domain.tellermgmt.model.command;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Functional equivalent of S18Command in the flat namespace requirement.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated) implements Command {}
