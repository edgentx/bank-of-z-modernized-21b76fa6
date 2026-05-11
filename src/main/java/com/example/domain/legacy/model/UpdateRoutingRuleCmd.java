package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int newRuleVersion,
        boolean dualProcessingAttempt
) implements Command {
    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId);
        Objects.requireNonNull(ruleId);
        Objects.requireNonNull(newTarget);
        Objects.requireNonNull(effectiveDate);
    }
}