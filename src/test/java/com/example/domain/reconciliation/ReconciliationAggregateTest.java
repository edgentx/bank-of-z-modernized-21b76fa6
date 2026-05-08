package com.example.domain.reconciliation;

import com.example.domain.reconciliation.model.ForceBalanceCmd;
import com.example.domain.reconciliation.model.ReconciliationAggregate;
import com.example.domain.reconciliation.model.ReconciliationBalancedEvent;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ReconciliationAggregate.
 * Covers the logic for reconciliation commands and events.
 */
class ReconciliationAggregateTest {

    @Test
    void givenUnknownCommand_whenExecute_thenThrowUnknownCommandException() {
        // Arrange
        var aggregate = new ReconciliationAggregate("batch-1");
        Object unknownCmd = new Object();

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> aggregate.execute((com.example.domain.shared.Command) unknownCmd));
    }

    @Test
    void givenValidForceBalanceCmd_whenExecute_thenEmitReconciliationBalancedEvent() {
        // Arrange
        String batchId = "batch-123";
        BigDecimal delta = BigDecimal.ZERO;
        var cmd = new ForceBalanceCmd(batchId, delta, Instant.now());
        var aggregate = new ReconciliationAggregate(batchId);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ReconciliationBalancedEvent);
        
        ReconciliationBalancedEvent event = (ReconciliationBalancedEvent) events.get(0);
        assertEquals(batchId, event.aggregateId());
        assertEquals("ReconciliationBalanced", event.type());
    }
}
