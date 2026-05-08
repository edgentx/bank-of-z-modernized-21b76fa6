package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to evaluate routing rules for a transaction.
 * Consolidated into domain.legacybridge.model per S-23 requirements.
 */
public record EvaluateRoutingCmd(
        String routeId,
        String transactionType,
        Map<String, Object> payload,
        int targetRulesVersion // Used to verify versioning invariant
) implements Command {
}