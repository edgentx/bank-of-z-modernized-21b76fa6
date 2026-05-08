package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to evaluate the routing destination for a transaction.
 * S-23: LegacyTransactionRoute Command.
 */
public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    Map<String, Object> payload,
    Integer targetRulesVersion,
    String explicitTargetSystem // Optional override to test dual-processing violation
) implements Command {}
