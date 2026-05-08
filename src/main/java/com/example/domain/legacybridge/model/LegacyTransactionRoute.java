package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentTarget;
    private int currentRuleVersion;
    private boolean isRouted;
    private boolean versioningViolation;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentTarget = "LEGACY";
        this.currentRuleVersion = 1;
        this.isRouted = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    // Test helper
    public void setVersioningViolation(boolean violation) {
        this.versioningViolation = violation;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Existing logic kept for context, implementing UpdateRoutingRuleCmd for S-24
        if (versioningViolation) {
             throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }
        
        // Simplified evaluation for test context
        var event = new RoutingEvaluatedEvent(
                cmd.routeId(),
                this.currentTarget,
                cmd.rulesVersion(),
                cmd.payload(),
                java.time.Instant.now()
        );
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant: A transaction must route to exactly one backend system
        if (cmd.newTarget() == null || (!cmd.newTarget().equals("MODERN") && !cmd.newTarget().equals("LEGACY"))) {
            throw new IllegalArgumentException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant: Routing rules must be versioned
        if (versioningViolation || cmd.ruleVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Apply state change
        this.currentTarget = cmd.newTarget();
        this.currentRuleVersion = cmd.ruleVersion();
        this.isRouted = true;

        var event = new RoutingRuleUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.ruleVersion(),
                cmd.effectiveDate(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
