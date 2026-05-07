package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Set;

/**
 * Command to initiate a teller session.
 * Context: S-18 (TellerSession)
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> permissions,
    String currentNavigationState
) implements Command {}
