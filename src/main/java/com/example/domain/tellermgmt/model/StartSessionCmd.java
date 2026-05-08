package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    boolean timedOut,
    String navState
) implements Command {}
