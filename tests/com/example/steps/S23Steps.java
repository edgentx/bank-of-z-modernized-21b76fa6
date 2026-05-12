package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-23 (EvaluateRoutingCmd).
 * Shared LegacyTransactionRoute Givens + the rejection @Then live in
 * {@link LegacyTransactionRouteSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link LegacyTransactionRouteSharedContext}.
 */
public class S23Steps {

    private final LegacyTransactionRouteSharedContext ctx;
    private final ScenarioContext sc;

    public S23Steps(LegacyTransactionRouteSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_EvaluateRoutingCmd_command_is_executed() {
        LegacyTransactionRoute aggregate = ctx.aggregate;
        Map<String, Object> payload = Map.of("source", "WEB", "amount", 100);
        EvaluateRoutingCmd command = new EvaluateRoutingCmd(aggregate.id(), "TRANSFER", payload, 1);
        try {
            ctx.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            sc.thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNull(sc.thrownException, "Should not throw exception");
        List<DomainEvent> events = ctx.resultingEvents;
        assertNotNull(events, "Events should not be null");
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof RoutingEvaluatedEvent);

        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) events.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals(ctx.aggregate.id(), event.aggregateId());
    }
}
