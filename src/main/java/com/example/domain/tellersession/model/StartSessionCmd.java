package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Context: S-18 (User Interface Navigation).
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated
) implements Command {}
