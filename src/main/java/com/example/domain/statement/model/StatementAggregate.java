package com.example.domain.statement.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;

/**
 * StatementAggregate — scaffold stub. Story-specific commands extend this.
 * Hand-bootstrapped on main to unblock domain command stories
 * (#600 follow-up while engineer-runner is built).
 */
public class StatementAggregate extends AggregateRoot {
  private final String id;

  public StatementAggregate(String id) { this.id = id; }

  @Override public String id() { return id; }

  @Override public List<DomainEvent> execute(Command cmd) {
    throw new UnknownCommandException(cmd);
  }
}
