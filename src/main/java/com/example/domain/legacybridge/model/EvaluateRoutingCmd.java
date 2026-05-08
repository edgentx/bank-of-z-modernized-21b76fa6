package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record EvaluateRoutingCmd(
        String routeId,
        String transactionType,
        Map<String, Object> payload,
        Integer ruleVersion
) implements Command {
}