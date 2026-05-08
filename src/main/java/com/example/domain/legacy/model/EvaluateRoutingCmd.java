package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

/**
 * Command to evaluate the routing rules for a transaction.
 * Used to determine if a transaction should be processed by the Legacy or Modern backend.
 */
public record EvaluateRoutingCmd(
    String transactionId,
    String transactionType,
    String payload,
    String targetSystem, // e.g., "LEGACY", "MODERN"
    int ruleVersion
) implements Command {}
