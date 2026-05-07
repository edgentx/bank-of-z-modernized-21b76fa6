package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Inherited authentication context is assumed via the TellerId validation against
 * a pre-authenticated token or principal state passed implicitly during command handling.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}