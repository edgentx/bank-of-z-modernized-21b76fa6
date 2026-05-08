package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {
    private LegacyTransactionRouteAggregate aggregate;
    private EvaluateRoutingCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRouteAggregate("route-1");
        // Ensure the aggregate meets the versioning invariant for success case
        aggregate.setRoutingVersion(1);
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transactionType_is_provided() {
        // Handled in the When block construction for simplicity, or stored here
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Handled in the When block construction
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_EvaluateRoutingCmd_command_is_executed() {
        // Construct valid command
        command = new EvaluateRoutingCmd("route-1", "PAYMENT", "payload-data", "modern");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals("modern", event.targetSystem());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_uniqueness() {
        aggregate = new LegacyTransactionRouteAggregate("route-2");
        aggregate.setRoutingVersion(1); // satisfy other rules
    }

    @When("the EvaluateRoutingCmd command is executed with invalid target")
    public void the_EvaluateRoutingCmd_command_is_executed_with_invalid_target() {
        // Target "dual" simulates violating the "exactly one" rule
        command = new EvaluateRoutingCmd("route-2", "PAYMENT", "payload", "dual");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("dual-processing"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRouteAggregate("route-3");
        // Version defaults to 0, simulating violation
    }

    @When("the EvaluateRoutingCmd command is executed on unversioned aggregate")
    public void the_EvaluateRoutingCmd_command_is_executed_on_unversioned_aggregate() {
        command = new EvaluateRoutingCmd("route-3", "PAYMENT", "payload", "legacy");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Then reused: the command is rejected with a domain error
}
