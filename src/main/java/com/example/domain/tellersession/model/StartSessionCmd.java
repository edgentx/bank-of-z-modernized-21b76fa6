package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a new Teller Session.
 * @param tellerId The authenticated ID of the teller.
 * @param terminalId The ID of the terminal the teller is using.
 * @param timestamp The timestamp of the request.
 */
public record StartSessionCmd(String tellerId, String terminalId, Instant timestamp) implements Command {

  public StartSessionCmd {
    Objects.requireNonNull(timestamp, "Timestamp must not be null");
  }
}