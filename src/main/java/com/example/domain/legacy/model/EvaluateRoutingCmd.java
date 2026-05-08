package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to evaluate the routing for a transaction.
 */
public record EvaluateRoutingCmd(String routeId, String transactionType, String payload, Integer ruleVersion, boolean isDualProcessingCandidate) implements Command {
    public EvaluateRoutingCmd {
        Objects.requireNonNull(routeId, "routeId cannot be null");
    }
}