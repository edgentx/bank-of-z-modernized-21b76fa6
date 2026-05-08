package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to evaluate the routing rules for a transaction.
 * Ensures versioning to support safe rollbacks and prevent dual-processing.
 */
public record EvaluateRoutingCmd(
        String routeId,
        String transactionType,
        Map<String, Object> payload,
        int rulesVersion
) implements Command {}
