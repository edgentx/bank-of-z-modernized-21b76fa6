package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record EvaluateRoutingCmd(String routeId, String transactionType, Map<String, Object> payload) implements Command {
    public EvaluateRoutingCmd {
        if (routeId == null || routeId.isBlank()) throw new IllegalArgumentException("routeId cannot be null/blank");
        if (transactionType == null || transactionType.isBlank()) throw new IllegalArgumentException("transactionType cannot be null/blank");
        // Payload can be empty for some routing logic checks, but not null
        if (payload == null) throw new IllegalArgumentException("payload cannot be null");
    }
}