package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Requires successful authentication prior to execution.
 */
public record StartSessionCmd(
    String aggregateId,
    String tellerId,
    String terminalId
) implements Command {}
