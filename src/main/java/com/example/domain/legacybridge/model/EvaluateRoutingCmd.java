package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to evaluate the routing destination for a transaction.
 * Used by S-23: EvaluateRoutingCmd on LegacyTransactionRoute.
 */
public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    Map<String, Object> payload
) implements Command {}
