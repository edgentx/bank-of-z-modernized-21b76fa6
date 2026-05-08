package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate for managing LegacyTransactionRoute.
 * S-23: Implement EvaluateRoutingCmd.
 */
public class LegacyTransactionRoute extends AggregateRoot {
  private final String routeId;
  private String targetSystem; // "modern" or "legacy"
  private int currentRuleVersion;

  public LegacyTransactionRoute(String routeId) {
    this.routeId = routeId;
  }

  @Override
  public String id() {
    return routeId;
  }

  @Override
  public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof EvaluateRoutingCmd c) {
      return evaluate(c);
    }
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> evaluate(EvaluateRoutingCmd cmd) {
    // Invariant: Rule version must be valid (simulating version check)
    if (cmd.ruleVersion() <= 0) {
      throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
    }

    // Determine target (Simplified logic for S-23: assume Legacy if payload is small, Modern if large/complex)
    String determinedTarget;
    if (cmd.payload() != null && cmd.payload().length() > 100) {
      determinedTarget = "modern";
    } else {
      determinedTarget = "legacy";
    }

    // Invariant: Must route to exactly ONE system
    if (determinedTarget == null || (!determinedTarget.equals("modern") && !determinedTarget.equals("legacy"))) {
      throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
    }

    var event = new RoutingEvaluatedEvent(
        cmd.routeId(),
        determinedTarget,
        cmd.transactionType(),
        cmd.ruleVersion(),
        Instant.now()
    );

    this.targetSystem = determinedTarget;
    this.currentRuleVersion = cmd.ruleVersion();

    addEvent(event);
    incrementVersion();
    return List.of(event);
  }

  public String getTargetSystem() { return targetSystem; }
  public int getCurrentRuleVersion() { return currentRuleVersion; }
}
