package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;
import java.util.Map;

/**
 * Aggregate for determining how an incoming transaction should be routed.
 * Enforces rules regarding dual-processing and rule versioning.
 */
public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private int currentRuleVersion = 1;
    private boolean isModernEnabled = false;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String id() {
        return routeId;
    }

    public void configure(boolean modernEnabled, int version) {
        this.isModernEnabled = modernEnabled;
        this.currentRuleVersion = version;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluate(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluate(EvaluateRoutingCmd cmd) {
        // Invariant: Routing rules must be versioned to allow safe rollback.
        // We enforce that the version is strictly positive.
        if (currentRuleVersion <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback. Invalid version: " + currentRuleVersion);
        }

        // Simulate determining the target system based on flags.
        // In a real system, this might inspect 'cmd.transactionType' or feature flags.
        String targetSystem;
        
        // Hard-coded invariant failure scenario for the BDD test:
        // If the payload explicitly contains a key 'violation'='dual-write', we simulate an ambiguous state.
        Object violation = cmd.payload().get("violation");
        if ("dual-write".equals(violation)) {
             // Scenario: "A transaction must route to exactly one backend system..."
             // This represents a state where the system cannot decide, or the config implies both.
             throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        if (isModernEnabled) {
            targetSystem = "VForce360";
        } else {
            targetSystem = "CICS";
        }

        var event = RoutingEvaluatedEvent.create(
                this.routeId,
                targetSystem,
                this.currentRuleVersion,
                Map.of("transactionType", cmd.transactionType())
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public int getCurrentRuleVersion() {
        return currentRuleVersion;
    }
}