package com.example.domain.routing.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate Root for Legacy Transaction Routing configuration.
 * Manages the rules that determine whether transactions go to CICS/IMS (Legacy) or VForce360 (Modern).
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int currentRuleVersion;
    private final Map<String, RuleConfig> rules = new HashMap<>();

    // Internal state for tracking targets to enforce invariants
    private static class RuleConfig {
        String target; // "MODERN" or "LEGACY"
        Instant effectiveDate;
        int version;
    }

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentRuleVersion = 0;
    }

    @Override
    public String id() {
        return routeId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return handleUpdateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleUpdateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant 1: Validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be null or empty");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be null or empty");
        }
        
        // Normalize target for check (accept lower/mixed case input if needed, strictly enforce upper for logic)
        String normalizedTarget = cmd.newTarget().toUpperCase();
        
        // Invariant 2: Must route to exactly one backend
        if (!("MODERN".equals(normalizedTarget) || "LEGACY".equals(normalizedTarget))) {
             throw new IllegalArgumentException("Invalid target: " + cmd.newTarget() + ". Must be MODERN or LEGACY.");
        }

        RuleConfig existing = rules.get(cmd.ruleId());
        int newVersion = 1;
        if (existing != null) {
            // Invariant 3: Safe Rollback / Versioning
            // Reject update if effective date is not strictly after the current version's date
            // or if we aren't incrementing the version logically.
            if (cmd.effectiveDate() == null) {
                throw new IllegalArgumentException("effectiveDate is required for versioning");
            }
            if (!cmd.effectiveDate().isAfter(existing.effectiveDate)) {
                throw new IllegalStateException("Cannot update rule: Effective date must be after current rule date to maintain versioning history.");
            }
            newVersion = existing.version + 1;
        } else {
            // New rule creation check
            if (cmd.effectiveDate() == null) {
                throw new IllegalArgumentException("effectiveDate is required for new rules");
            }
        }

        // Apply State Change
        RuleConfig updatedConfig = new RuleConfig();
        updatedConfig.target = normalizedTarget;
        updatedConfig.effectiveDate = cmd.effectiveDate();
        updatedConfig.version = newVersion;
        rules.put(cmd.ruleId(), updatedConfig);
        
        currentRuleVersion++; // Aggregate version bump

        // Create Event
        RoutingRuleUpdatedEvent event = new RoutingRuleUpdatedEvent(
            this.routeId,
            cmd.ruleId(),
            normalizedTarget,
            cmd.effectiveDate(),
            newVersion,
            Instant.now()
        );

        addEvent(event);
        incrementVersion(); // From AggregateRoot
        return List.of(event);
    }

    // Getters for testing/verification
    public int getCurrentRuleVersion(String ruleId) {
        RuleConfig c = rules.get(ruleId);
        return c == null ? 0 : c.version;
    }
}