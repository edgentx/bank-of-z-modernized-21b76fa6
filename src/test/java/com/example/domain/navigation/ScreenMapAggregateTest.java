package com.example.domain.navigation;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for ScreenMap Aggregate.
 * Covering S-21 Acceptance Criteria.
 */
class ScreenMapAggregateTest {

    @Test
    void testExecuteRenderScreenCmd_Success() {
        // Given
        String aggregateId = "map-1";
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", "WEB");

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        
        DomainEvent event = events.get(0);
        assertInstanceOf(ScreenRenderedEvent.class, event);
        
        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals(aggregateId, renderedEvent.aggregateId());
        assertEquals("LOGIN", renderedEvent.screenId());
        assertEquals("WEB", renderedEvent.deviceType());
        assertNotNull(renderedEvent.occurredAt());
    }

    @Test
    void testExecuteRenderScreenCmd_Rejected_MissingScreenId() {
        // Given: A ScreenMap aggregate that violates: All mandatory input fields must be validated
        String aggregateId = "map-2";
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);
        RenderScreenCmd cmd = new RenderScreenCmd(null, "WEB"); // screenId is null

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("screenId is required"));
    }

    @Test
    void testExecuteRenderScreenCmd_Rejected_MissingDeviceType() {
        // Given: A ScreenMap aggregate that violates: All mandatory input fields must be validated
        String aggregateId = "map-3";
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", ""); // deviceType is blank

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("deviceType is required"));
    }

    @Test
    void testExecuteRenderScreenCmd_Rejected_BmsConstraintsScreenId() {
        // Given: A ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints
        String aggregateId = "map-4";
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);
        // "SUPERLONGSCREENID" is > 8 chars
        RenderScreenCmd cmd = new RenderScreenCmd("SUPERLONGSCREENID", "WEB");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("exceeds legacy BMS field length limit"));
    }

    @Test
    void testExecuteRenderScreenCmd_Rejected_BmsConstraintsDeviceType() {
        // Given: A ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints
        String aggregateId = "map-5";
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);
        // "ANDROIDTV" is > 8 chars
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", "ANDROIDTV");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("exceeds legacy BMS field length limit"));
    }
}
