package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int rulesVersion
) implements Command {}
