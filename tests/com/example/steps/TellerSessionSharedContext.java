package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;

import java.util.List;

/**
 * Scenario-scoped shared state for TellerSession-aggregate Cucumber steps
 * (S-18, S-19, S-20). Injected via cucumber-picocontainer so the multiple
 * story step classes can read/write the same aggregate instance after the
 * shared @Given step seeds it.
 */
public class TellerSessionSharedContext {
    public TellerSessionAggregate aggregate;
    public List<DomainEvent> resultingEvents;
}
