package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Validated by the TellerSession aggregate.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
