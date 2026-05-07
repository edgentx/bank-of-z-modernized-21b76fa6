package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Part of User Interface Navigation (S-18).
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
