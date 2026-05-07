package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartReconciliationCmd(String batchId, Instant start, Instant end) implements Command {
  public StartReconciliationCmd {
    if (batchId == null || batchId.isBlank()) throw new IllegalArgumentException("batchId required");
    if (start == null) throw new IllegalArgumentException("start required");
    if (end == null) throw new IllegalArgumentException("end required");
    if (end.isBefore(start)) throw new IllegalArgumentException("end must be after start");
  }
}