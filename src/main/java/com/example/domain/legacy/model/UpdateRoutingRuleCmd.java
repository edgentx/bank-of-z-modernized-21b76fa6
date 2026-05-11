package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a specific routing rule.
 * Used to shift traffic between legacy and modern systems.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int newRuleVersion
) implements Command {}
