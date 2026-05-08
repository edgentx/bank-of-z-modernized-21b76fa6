package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Created by S-18.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated, // Flag to simulate auth check for BDD
    boolean stale,         // Flag to simulate stale state for BDD
    String navigationState // String to check nav state validity
) implements Command {}
