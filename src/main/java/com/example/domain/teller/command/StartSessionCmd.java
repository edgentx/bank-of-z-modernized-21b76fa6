package com.example.domain.teller.command;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Story S-18: StartSessionCmd
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String initialNavigationState
) implements Command {}
