package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-1");
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // Context loaded in When step
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Context loaded in When step
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("ROUTE-1", "TX_TYPE_A", "{}", 1, false);
        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("ROUTE-1", event.aggregateId());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-2");
    }

    @When("the EvaluateRoutingCmd command is executed for dual processing violation")
    public void the_command_is_executed_for_dual_processing_violation() {
        // Using the flag to simulate the violation condition
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("ROUTE-2", "TX_TYPE_B", "{}", 1, true); 
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding dual processing")
    public void the_command_is_rejected_with_a_domain_error_regarding_dual_processing() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("dual-processing"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-3");
    }

    @When("the EvaluateRoutingCmd command is executed for versioning violation")
    public void the_command_is_executed_for_versioning_violation() {
        // Using version 0 to simulate the violation condition
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("ROUTE-3", "TX_TYPE_C", "{}", 0, false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding versioning")
    public void the_command_is_rejected_with_a_domain_error_regarding_versioning() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("versioned"));
    }
}
