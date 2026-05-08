package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

/**
 * Command to evaluate routing rules for a transaction.
 */
public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    String payload,
    Integer ruleVersion
) implements Command {}
