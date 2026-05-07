package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session following successful authentication.
 * Context: S-18 User Interface Navigation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentNavigationState
) implements Command {}
