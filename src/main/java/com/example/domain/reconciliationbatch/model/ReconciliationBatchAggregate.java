package com.example.domain.reconciliationbatch.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;

/**
 * ReconciliationBatchAggregate — scaffold stub. Story-specific commands extend this.
 * Hand-bootstrapped on main to unblock domain command stories
 * (#600 follow-up while engineer-runner is built).
 */
public class ReconciliationBatchAggregate extends AggregateRoot {
  private final String id;

  public ReconciliationBatchAggregate(String id) { this.id = id; }

  @Override public String id() { return id; }

  @Override public List<DomainEvent> execute(Command cmd) {
    throw new UnknownCommandException("ReconciliationBatchAggregate: " + cmd.getClass().getSimpleName());
  }
}
