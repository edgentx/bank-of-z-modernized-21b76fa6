package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    String navigationContext // Represents the current screen or operation context
) implements Command {}
