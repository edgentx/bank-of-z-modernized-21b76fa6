package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;

import java.util.List;

/**
 * Scenario-scoped shared state for LegacyTransactionRoute-aggregate
 * Cucumber steps (S-23 EvaluateRouting, S-24 UpdateRoutingRule).
 * Injected via cucumber-picocontainer so the multiple story step classes
 * can read/write the same aggregate after the shared @Given step seeds it.
 */
public class LegacyTransactionRouteSharedContext {
    public final InMemoryLegacyTransactionRouteRepository repository =
            new InMemoryLegacyTransactionRouteRepository();
    public LegacyTransactionRoute aggregate;
    public List<DomainEvent> resultingEvents;
}
