package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Used after successful authentication via CICS/IMS or Spring Security.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId
) implements Command {}
