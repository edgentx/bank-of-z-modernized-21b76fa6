package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Set;

/**
 * Command to initiate a Teller Session.
 * Story: S-18
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Set<String> roles // AuthZ context
) implements Command {}
