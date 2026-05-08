package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to evaluate the routing destination for a transaction.
 * S-23: Implement EvaluateRoutingCmd on LegacyTransactionRoute.
 */
public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    Map<String, Object> payload,
    int targetRulesVersion
) implements Command {
}
