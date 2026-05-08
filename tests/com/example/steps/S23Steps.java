package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private InMemoryLegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private EvaluateRoutingCmd command;
    private Exception caughtException;
    private RoutingEvaluatedEvent resultEvent;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRoute("ROUTE-1");
        repository.save(aggregate);
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transactionType_is_provided() {
        // Transaction type is part of the command, initialized in @When
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Payload is part of the command, initialized in @When
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("ROUTE-DUAL-ERR");
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("ROUTE-VER-ERR");
        aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_EvaluateRoutingCmd_command_is_executed() {
        try {
            // Determine context based on aggregate state (Default vs Violations)
            String routeId = aggregate.id();
            
            // Default valid payload
            Map<String, Object> payload = Map.of("source", "WEB", "amount", 100);
            
            // Default valid version
            int version = 1;

            command = new EvaluateRoutingCmd(routeId, "TRANSFER", payload, version);
            
            var events = aggregate.execute(command);
            if (!events.isEmpty()) {
                resultEvent = (RoutingEvaluatedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvent, "Expected event to be emitted");
        assertEquals("routing.evaluated", resultEvent.type());
        assertEquals(aggregate.id(), resultEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
