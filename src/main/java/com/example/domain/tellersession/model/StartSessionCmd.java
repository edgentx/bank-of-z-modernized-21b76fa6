package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record StartSessionCmd(String tellerId, String terminalId) implements Command {
  public StartSessionCmd {
    if (tellerId == null || tellerId.isBlank()) {
      throw new IllegalArgumentException("Teller ID cannot be null or blank");
    }
    if (terminalId == null || terminalId.isBlank()) {
      throw new IllegalArgumentException("Terminal ID cannot be null or blank");
    }
  }
}