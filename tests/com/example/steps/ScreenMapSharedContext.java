package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.shared.DomainEvent;

import java.util.List;

/**
 * Scenario-scoped shared state for ScreenMap-aggregate Cucumber steps
 * (S-21 RenderScreen, S-22 ValidateScreenInput). Injected via
 * cucumber-picocontainer so the multiple story step classes can read/write
 * the same aggregate after the shared @Given step seeds it.
 */
public class ScreenMapSharedContext {
    public ScreenMapAggregate aggregate;
    public List<DomainEvent> resultingEvents;
}
