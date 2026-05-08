package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private LegacyTransactionRouteAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Success
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRouteAggregate("route-123");
    }

    @And("a valid transactionType is provided")
    public void a_valid_transactionType_is_provided() {
        // State stored in context for command execution
    }

    @And("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // State stored in context
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_EvaluateRoutingCmd_command_is_executed() {
        try {
            var cmd = new EvaluateRoutingCmd("route-123", "WIRE", "modern", 1, "{}");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        assertEquals("routing.evaluated", resultEvents.get(0).type());
    }

    // Scenario 2: Dual-processing rejection
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRouteAggregate("route-bad");
    }

    // Reuse When from above

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        // Command setup for the specific scenario context
        var cmd = new EvaluateRoutingCmd("route-bad", "WIRE", null, 1, "{}"); // Null target triggers violation
        
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    // Scenario 3: Versioning rejection
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRouteAggregate("-route-nover");
    }

    // Reuse When and Then logic effectively by executing the specific bad command in the test logic
    // For Cucumber clarity, we map the generic 'When' to the specific context in the steps or use a table.
    // Here we assume the violation implies a specific command structure.
    
    // Helper for Scenario 3 specific execution (if needed separate from Scenario 2 flow)
    @When("the EvaluateRoutingCmd command is executed with bad version")
    public void the_EvaluateRoutingCmd_command_is_executed_with_bad_version() {
         assertThrows(IllegalArgumentException.class, () -> {
            // Version 0 or negative is invalid
            aggregate.execute(new EvaluateRoutingCmd("route-nover", "WIRE", "legacy", 0, "{}"));
        });
    }
}
