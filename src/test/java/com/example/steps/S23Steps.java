package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private String transactionType;
    private String payload;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        this.transactionType = "DEPOSIT";
    }

    @And("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        this.payload = "{\"amount\": 100}";
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("route-123", transactionType, payload, 1, false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        Assertions.assertEquals("routing.evaluated", event.type());
        Assertions.assertEquals("route-123", event.aggregateId());
        Assertions.assertNotNull(event.targetSystem());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_aggregate_with_dual_processing_violation() {
        this.aggregate = new LegacyTransactionRoute("route-dual");
        // Setup state if needed
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_aggregate_with_version_violation() {
        this.aggregate = new LegacyTransactionRoute("route-unversioned");
        // Setup state if needed
    }

    @When("the EvaluateRoutingCmd command is executed with dual processing violation")
    public void execute_cmd_dual_processing() {
        // command configured to trigger dual processing violation logic
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("route-dual", "WILDCARD", "{}", 1, true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the EvaluateRoutingCmd command is executed with version violation")
    public void execute_cmd_version_violation() {
        // command configured to trigger version violation logic (null version)
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("route-unversioned", "DEPOSIT", "{}", null, false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Check that the error message matches the expected invariant violation
        Assertions.assertTrue(caughtException.getMessage().contains("dual-processing") 
                           || caughtException.getMessage().contains("rollback"));
    }
}