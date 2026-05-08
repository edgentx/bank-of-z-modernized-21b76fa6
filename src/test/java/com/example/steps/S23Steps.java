package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private final InMemoryLegacyTransactionRouteRepository repo = new InMemoryLegacyTransactionRouteRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        // Initialize state if necessary, though constructor handles defaults
    }

    @Given("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // Context setup for scenario, handled in When block via command construction
    }

    @Given("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // Context setup
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-dual-violation");
        // The violation is triggered by the command payload type "DUAL_ROUTE" in the When step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-version-violation");
        // The violation is triggered by sending a 0 or null ruleVersion in the When step
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            // Determining command parameters based on the aggregate ID or context if needed
            // For simplicity in BDD, we construct a command that should pass, unless the specific Given implies a fail condition
            // However, we need to differentiate scenarios. 
            // Scenario 1: Success -> Normal payload
            // Scenario 2: Dual Processing -> "DUAL_ROUTE" type
            // Scenario 3: Versioning -> version 0

            EvaluateRoutingCmd cmd;
            if (aggregate.id().equals("route-dual-violation")) {
                cmd = new EvaluateRoutingCmd(aggregate.id(), "DUAL_ROUTE", "test-payload", 1);
            } else if (aggregate.id().equals("route-version-violation")) {
                cmd = new EvaluateRoutingCmd(aggregate.id(), "PAYMENT_V1", "test-payload", 0);
            } else {
                cmd = new EvaluateRoutingCmd(aggregate.id(), "PAYMENT_V2", "test-payload", 1);
            }

            this.resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        Assertions.assertEquals("routing.evaluated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Check if it's an IllegalStateException (domain invariant violation)
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
