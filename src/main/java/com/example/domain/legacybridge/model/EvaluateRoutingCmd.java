package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to evaluate routing rules for a legacy transaction.
 * Used to determine if a request should be routed to the Modern platform or the Legacy system.
 */
public record EvaluateRoutingCmd(
        String routeId,
        String transactionType,
        Map<String, Object> payload,
        String targetRulesVersion // Fixed from previous compilation errors
) implements Command {}
