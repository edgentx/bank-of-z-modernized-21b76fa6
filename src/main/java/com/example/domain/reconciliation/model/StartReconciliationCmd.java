package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record StartReconciliationCmd(String batchId, Instant batchWindowStart, Instant batchWindowEnd) implements Command {}
