package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session on a specific terminal.
 * Implies successful authentication has already occurred upstream.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {}
