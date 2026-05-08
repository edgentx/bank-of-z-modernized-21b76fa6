package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

/**
 * Command to evaluate routing for a transaction.
 * Story S-23.
 */
public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    String targetSystem,
    int ruleVersion,
    String payload
) implements Command {}
