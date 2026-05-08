package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Set;

/**
 * Command to initiate a new teller session.
 * Context: S-18 User Interface Navigation.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    Set<String> roles,          // AuthZ context (e.g. TELLER, SUPERVISOR)
    Long lastActivityTimestamp, // For timeout validation check
    String navigationState      // Context validation (e.g. IDLE, TRANSACTION_IN_PROGRESS)
) implements Command {}
