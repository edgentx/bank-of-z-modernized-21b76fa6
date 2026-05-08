package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record UpdateRoutingRuleCmd(String routeId, String newTarget, Instant effectiveDate) implements Command {
    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId, "routeId cannot be null");
        Objects.requireNonNull(newTarget, "newTarget cannot be null");
        Objects.requireNonNull(effectiveDate, "effectiveDate cannot be null");
        
        if (routeId.isBlank()) {
            throw new IllegalArgumentException("routeId cannot be blank");
        }
        if (newTarget.isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be blank");
        }
    }
}