package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to terminate the current Teller Session.
 */
public class EndSessionCmd implements Command {

  private final String sessionId;

  public EndSessionCmd(String sessionId) {
    this.sessionId = Objects.requireNonNull(sessionId, "sessionId cannot be null");
  }

  public String sessionId() {
    return sessionId;
  }
}
