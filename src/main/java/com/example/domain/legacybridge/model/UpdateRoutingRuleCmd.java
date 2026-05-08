package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record UpdateRoutingRuleCmd(
    String routeId,
    String newTarget,
    Instant effectiveDate
) implements Command {

    public UpdateRoutingRuleCmd {
        Objects.requireNonNull(routeId);
        Objects.requireNonNull(newTarget);
        Objects.requireNonNull(effectiveDate);
    }
}
