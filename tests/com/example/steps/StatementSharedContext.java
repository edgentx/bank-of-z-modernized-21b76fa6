package com.example.steps;

import com.example.domain.statement.model.StatementAggregate;
import com.example.mocks.InMemoryStatementRepository;

/**
 * Scenario-scoped shared state for Statement-aggregate Cucumber steps.
 * Injected via cucumber-picocontainer so multiple step classes can read/write
 * the same aggregate and exception state within a single scenario.
 */
public class StatementSharedContext {
    public final InMemoryStatementRepository repository = new InMemoryStatementRepository();
    public StatementAggregate aggregate;
    public Throwable thrownException;
}
