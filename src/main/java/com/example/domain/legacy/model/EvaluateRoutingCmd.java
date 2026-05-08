package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    Map<String, Object> payload,
    String targetSystem,
    int ruleVersion
) implements Command {}
