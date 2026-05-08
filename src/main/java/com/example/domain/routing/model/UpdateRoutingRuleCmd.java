package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int version
) implements Command {}
