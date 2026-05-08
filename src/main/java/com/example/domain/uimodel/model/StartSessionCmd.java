package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller session.
 * Belongs to the user-interface-navigation (UI Model) context.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
