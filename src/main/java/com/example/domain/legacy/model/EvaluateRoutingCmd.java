package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    String payload,
    int ruleVersion,
    boolean dualProcessingAttempt
) implements Command {}
