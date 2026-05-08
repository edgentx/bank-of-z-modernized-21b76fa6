package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a routing rule for a specific legacy transaction route.
 * Used to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate
) implements Command {}
