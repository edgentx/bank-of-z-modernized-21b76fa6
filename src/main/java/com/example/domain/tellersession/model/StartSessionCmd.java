package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.UUID;

/**
 * Command to initiate a teller session.
 * Validated against invariants: Teller AuthZ, Terminal validity, Context validity.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    String operationalContext
) implements Command {}
