package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Internal command to bootstrap the session for testing or initial login flow.
 * Not part of S-19 directly but required to put the Aggregate in a valid state.
 */
public record InitiateTellerSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
