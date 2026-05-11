package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update the routing configuration for a transaction type.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate
) implements Command {}
