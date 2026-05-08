package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * BAM S-18: StartSessionCmd.
 */
public record StartSessionCmd(
    String tellerSessionId,
    String tellerId,
    String terminalId
) implements Command {}
