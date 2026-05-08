package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

public record EvaluateRoutingCmd(
    String routeId,
    String transactionType,
    String payload,
    String targetSystem
) implements Command {}
