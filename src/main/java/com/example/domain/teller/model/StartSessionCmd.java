package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Validated by TellerSession aggregate invariants.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
  public StartSessionCmd {
    Objects.requireNonNull(sessionId, "sessionId cannot be null");
    Objects.requireNonNull(tellerId, "tellerId cannot be null");
    Objects.requireNonNull(terminalId, "terminalId cannot be null");
  }
}