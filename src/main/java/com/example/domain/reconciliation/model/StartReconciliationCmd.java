package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Set;

public record StartReconciliationCmd(
    String batchId,
    Instant windowStart,
    Instant windowEnd,
    Set<String> accountIds
) implements Command {}