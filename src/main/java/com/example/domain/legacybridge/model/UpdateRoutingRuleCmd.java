package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate
) implements Command {}
