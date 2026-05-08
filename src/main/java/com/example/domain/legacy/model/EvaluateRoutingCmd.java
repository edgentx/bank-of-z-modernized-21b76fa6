package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to evaluate routing rules for a specific transaction.
 */
public record EvaluateRoutingCmd(
        String routeId,
        String transactionType,
        Map<String, Object> payload,
        String targetSystem,
        Integer ruleVersion
) implements Command {}
