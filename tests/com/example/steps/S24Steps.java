package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-24 (UpdateRoutingRuleCmd).
 * Shared LegacyTransactionRoute Givens + the rejection @Then live in
 * {@link LegacyTransactionRouteSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link LegacyTransactionRouteSharedContext}.
 */
public class S24Steps {

    private final LegacyTransactionRouteSharedContext ctx;
    private final ScenarioContext sc;

    public S24Steps(LegacyTransactionRouteSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        LegacyTransactionRoute aggregate = ctx.aggregate;
        UpdateRoutingRuleCmd command = new UpdateRoutingRuleCmd(
                aggregate.id(),
                "MODERN",
                Instant.parse("2026-06-01T00:00:00Z"),
                1
        );
        try {
            ctx.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            sc.thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNull(sc.thrownException, "Should not throw exception");
        List<DomainEvent> events = ctx.resultingEvents;
        assertNotNull(events, "Expected routing.updated event to be emitted");
        assertEquals(1, events.size());
        DomainEvent emitted = events.get(0);
        assertTrue(emitted instanceof RoutingUpdatedEvent);

        RoutingUpdatedEvent event = (RoutingUpdatedEvent) emitted;
        assertEquals("routing.updated", event.type());
        assertEquals(ctx.aggregate.id(), event.aggregateId());
        assertEquals("MODERN", event.newTarget());
    }
}
