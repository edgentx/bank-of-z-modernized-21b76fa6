package com.example.domain.routing.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to evaluate the routing rules for a specific transaction type.
 * Part of Story S-23.
 */
public record EvaluateRoutingCmd(String routeId, String transactionType, Map<String, Object> payload) implements Command {
}