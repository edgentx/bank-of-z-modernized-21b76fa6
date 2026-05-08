package com.example.domain.routing.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    String payload,
    String targetSystem,
    int ruleVersion
) implements Command {}