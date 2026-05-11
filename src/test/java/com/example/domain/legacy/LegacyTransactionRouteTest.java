package com.example.domain.legacy;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for S-24: UpdateRoutingRuleCmd implementation.
 * Written in TDD Red Phase.
 */
class LegacyTransactionRouteTest {

    // ========================= Scenario 1: Success =========================
    @Test
    void shouldEmitRoutingUpdatedEventWhenCommandIsValid() {
        // Given
        String routeId = "ROUTE-101";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);

        String ruleId = "RULE-PRIORITY-1";
        String newTarget = "VForce360";
        Instant effectiveDate = Instant.now();

        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertNotNull(events, "Event list should not be null");
        assertEquals(1, events.size(), "Exactly one event should be emitted");

        DomainEvent domainEvent = events.get(0);
        assertTrue(domainEvent instanceof RoutingUpdatedEvent, "Event must be RoutingUpdatedEvent");

        RoutingUpdatedEvent routedEvent = (RoutingUpdatedEvent) domainEvent;
        assertEquals("RoutingUpdated", routedEvent.type());
        assertEquals(routeId, routedEvent.aggregateId());
        assertEquals(ruleId, routedEvent.ruleId());
        assertEquals(newTarget, routedEvent.newTarget());
        assertEquals(effectiveDate, routedEvent.effectiveDate());
        assertNotNull(routedEvent.occurredAt());

        // Verify aggregate state mutation (if implemented)
        // Assuming getters exist or checking uncommitted events via AggregateRoot logic
        assertEquals(1, aggregate.uncommittedEvents().size());
    }

    // ========================= Scenario 2: Rejection (Dual Processing) =========================
    @Test
    void shouldRejectCommandWhenTargetIsDualProcessing() {
        // Given
        String routeId = "ROUTE-DUAL";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);

        // Simulate a violation: Using a specific target string that implies dual processing
        // or setting internal state if the aggregate allows mutation.
        // Here we rely on the Command payload to trigger the validation logic.
        String dualTarget = "BOTH";

        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            routeId,
            "RULE-BAD",
            dualTarget, // The system that causes dual processing
            Instant.now()
        );

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("exactly one backend system"));
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }

    // ========================= Scenario 3: Rejection (Versioning/Rollback) =========================
    @Test
    void shouldRejectCommandWhenRoutingRulesAreNotVersioned() {
        // Given
        String routeId = "ROUTE-NO-VER";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);

        // Scenario implies the aggregate state or command context is missing version info.
        // We pass a null/blank effectiveDate or invalid context to simulate this invariant failure.
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            routeId,
            "RULE-NO-VER",
            "Modern",
            null // Null effective date might break versioning/rollback logic
        );

        // When & Then
        // We expect a domain error (IllegalArgumentException or IllegalStateException)
        Exception ex = assertThrows(Exception.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("versioned"));
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }

    @Test
    void shouldThrowUnknownCommandForUnsupportedCommand() {
        // Given
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute("TEST");
        String invalidCmd = "Not a Command object";

        // Note: This test verifies the behavior of the Aggregate Root's dispatch logic.
        // Since execute() takes a Command interface, we pass a dummy implementation or rely on the compiler.
        // However, `execute` signature in aggregate takes `Command`. 
        // This test ensures dispatch table is properly updated.
        
        // We create a dummy command implementation inline
        Command dummyCmd = new Command() {};

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(dummyCmd);
        });
    }
}
