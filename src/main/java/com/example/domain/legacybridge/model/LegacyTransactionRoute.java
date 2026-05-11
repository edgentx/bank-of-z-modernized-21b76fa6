package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private boolean dualProcessingViolation;
    private boolean versioningViolation;
    private boolean evaluated;
    private String targetSystem;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
        this.evaluated = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    public void markDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }

    public void markVersioningViolation() {
        this.versioningViolation = true;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant 1: Prevent dual-processing
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Versioning check
        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }
        
        if (cmd.version() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Basic validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId is required");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget is required");
        }

        var event = new RoutingUpdatedEvent(
                null, // eventId generated in constructor
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                cmd.version(),
                Instant.now()
        );

        // Update state (implied by the event)
        // Note: The event represents the change in routing configuration.
        // If we need to update internal state to reflect this new rule immediately:
        // this.targetSystem = cmd.newTarget();

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Logic from existing file preserved to not break other tests/features
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        if (cmd.rulesVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }
        if (versioningViolation) {
             throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }
        if (evaluated) {
            throw new IllegalStateException("Routing already evaluated for this route.");
        }

        String target = "LEGACY";
        if (cmd.payload() != null && cmd.payload().containsKey("forceModern")) {
            target = "MODERN";
        }

        var event = new RoutingEvaluatedEvent(
                cmd.routeId(),
                target,
                cmd.rulesVersion(),
                cmd.payload(),
                java.time.Instant.now()
        );

        this.evaluated = true;
        this.targetSystem = target;
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public String getTargetSystem() {
        return targetSystem;
    }
}
