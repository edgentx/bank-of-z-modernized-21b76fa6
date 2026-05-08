package com.example.domain.legacy.model;

import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for UpdateRoutingRuleCmd.
 */
class LegacyTransactionRouteTest {

    // Scenario: Successfully execute UpdateRoutingRuleCmd
    @Test
    void whenExecuteValidUpdateRoutingRuleCmd_thenRoutingUpdatedEventEmitted() {
        // Given
        String routeId = "route-legacy-1";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        String ruleId = "RULE-102";
        String newTarget = "VForce360";
        Instant effectiveDate = Instant.now().plusSeconds(3600);
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate);

        // When
        List events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        
        Object eventObj = events.get(0);
        assertInstanceOf(RoutingUpdatedEvent.class, eventObj);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) eventObj;
        assertEquals(routeId, event.aggregateId());
        assertEquals(ruleId, event.ruleId());
        assertEquals(newTarget, event.newTarget());
        assertEquals("RoutingUpdated", event.type());
        assertNotNull(event.occurredAt());
    }

    // Scenario: UpdateRoutingRuleCmd rejected — Dual processing violation
    @Test
    void givenDualProcessingViolation_whenExecuteUpdateRoutingRuleCmd_thenThrowsIllegalStateException() {
        // Given
        String routeId = "route-dual-violation";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        // Force the aggregate into a violating state via existing API or a test seam.
        // Assuming the aggregate has a mechanism or state that defaults to safe, we assume the 
        // implementation will handle the check. Here we rely on the implementation throwing.
        // If there is a specific setter to force the violation, we would use it.
        // For this test to fail in RED phase, we assume the Command handler must perform the check.
        
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, "RULE-101", "INVALID_DUAL_TARGET", Instant.now());

        // When & Then
        // The business requirement states: "A transaction must route to exactly one backend system".
        // If the command implies dual processing (e.g. via the newTarget string), it should fail.
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("exactly one backend system"));
    }

    // Scenario: UpdateRoutingRuleCmd rejected — Versioning violation
    @Test
    void givenVersioningViolation_whenExecuteUpdateRoutingRuleCmd_thenThrowsIllegalArgumentException() {
        // Given
        String routeId = "route-version-violation";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        // Assuming the 'ruleId' or internal state determines the version.
        // If we pass a command that implies a bad version (e.g., non-positive in a parsed ID),
        // or if the aggregate state is already bad.
        // For Red phase, we trigger the expected validation logic.
        
        // Let's assume ruleId "RULE-0" or empty implies a versioning issue for the test.
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, "RULE-0", "VForce360", Instant.now());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("versioned"));
    }
    
    @Test
    void whenExecuteUnknownCommand_thenThrowsUnknownCommandException() {
        // Given
        String routeId = "route-unknown";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        Command unknownCmd = new Command() {}; // Anonymous command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}