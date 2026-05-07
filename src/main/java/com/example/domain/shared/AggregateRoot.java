package com.example.domain.shared;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public abstract class AggregateRoot implements Aggregate {
  private final List<DomainEvent> uncommitted = new ArrayList<>();
  private int version;
  protected void addEvent(DomainEvent e) { uncommitted.add(e); }
  public List<DomainEvent> uncommittedEvents() { return Collections.unmodifiableList(uncommitted); }
  public void clearEvents() { uncommitted.clear(); }
  @Override public int getVersion() { return version; }
  protected void incrementVersion() { version++; }
}
