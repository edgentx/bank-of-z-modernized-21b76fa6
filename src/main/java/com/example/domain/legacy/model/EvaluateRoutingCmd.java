package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

public record EvaluateRoutingCmd(String routeId, String transactionType, String payload, String targetSystemHint, int ruleVersion) implements Command {
}