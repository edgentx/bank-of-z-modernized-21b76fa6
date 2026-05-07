package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.time.Instant;

public record StartReconciliationCmd(
    String batchId,
    Instant windowStart,
    Instant windowEnd,
    String operatorId
) implements Command {}