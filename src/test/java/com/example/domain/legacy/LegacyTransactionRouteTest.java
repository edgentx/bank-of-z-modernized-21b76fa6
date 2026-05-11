package com.example.domain.legacy;

import com.example.domain.legacy.command.UpdateRoutingRuleCmd;
import com.example.domain.legacy.event.RoutingUpdatedEvent;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LegacyTransactionRoute aggregate.
 * Context: Story S-24 - Implement UpdateRoutingRuleCmd
 */
class LegacyTransactionRouteTest {

    @Test
    void execute_UpdateRoutingRuleCmd_Successfully_EmitsRoutingUpdatedEvent() {
        // Arrange
        String routeId = "route-legacy-1";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        String ruleId = "rule-101";
        String newTarget = "VForce360"; // Modern system
        Instant effectiveDate = Instant.now();
        int newVersion = 2;

        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate, newVersion);

        // Act
        List<?> events = aggregate.execute(cmd);

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        
        Object eventObj = events.get(0);
        assertTrue(eventObj instanceof RoutingUpdatedEvent, "Event should be RoutingUpdatedEvent");
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) eventObj;
        assertEquals(routeId, event.aggregateId());
        assertEquals(ruleId, event.ruleId());
        assertEquals(newTarget, event.newTarget());
        assertEquals(effectiveDate, event.effectiveDate());
        assertEquals(newVersion, event.newVersion());
        assertEquals("RoutingUpdated", event.type());
        assertNotNull(event.occurredAt());
    }

    @Test
    void execute_UpdateRoutingRuleCmd_Rejected_WhenDualProcessingAttempted() {
        // Arrange
        String routeId = "route-dual-1";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        // Simulate the state where the aggregate detects a dual processing configuration
        // In the actual implementation, this state might be internal to the aggregate.
        // For the sake of the test suite, we execute the command which triggers the invariant check.
        
        String invalidTarget = "DUAL_PROCESSING"; // Simulating a violation pattern or state
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            routeId, 
            "rule-dual", 
            invalidTarget, 
            Instant.now(), 
            1
        );

        // Act & Assert
        // The aggregate logic checks if the target implies dual routing (modern AND legacy simultaneously)
        // For this test, we rely on the aggregate throwing an IllegalStateException for the specific invariant.
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("exactly one backend system"));
    }

    @Test
    void execute_UpdateRoutingRuleCmd_Rejected_WhenVersioningInvalid() {
        // Arrange
        String routeId = "route-version-1";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        int invalidVersion = 0; // Violates "Routing rules must be versioned to allow safe rollback"
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            routeId, 
            "rule-bad-version", 
            "VForce360", 
            Instant.now(), 
            invalidVersion
        );

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("versioned"));
    }

    @Test
    void execute_UnknownCommand_ThrowsException() {
        // Arrange
        String routeId = "route-unknown";
        LegacyTransactionRoute aggregate = new LegacyTransactionRoute(routeId);
        
        String unknownCmd = "NotACommand";

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}
