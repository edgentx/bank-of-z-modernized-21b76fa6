package com.example.steps;

import com.example.domain.legacy.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S23Steps {
    private LegacyTransactionRouteAggregate aggregate;
    private Exception thrownException;
    private RoutingEvaluatedEvent lastEvent;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRouteAggregate("test-route-1");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRouteAggregate("test-route-dual");
        // We force the aggregate into a state where dual processing is flagged/required by business logic
        // by using the specific testing constructor.
        this.aggregate.markAsDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRouteAggregate("test-route-version");
        // Force the aggregate into a state where versioning is invalid
        this.aggregate.markAsInvalidVersion();
    }

    @And("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // No-op, implicit in command creation
    }

    @And("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // No-op, implicit in command creation
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            EvaluateRoutingCmd cmd = new EvaluateRoutingCmd(aggregate.id(), "WIRE_TRANSFER", "{}");
            List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (RoutingEvaluatedEvent) events.get(0);
            }
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        Assertions.assertNotNull(lastEvent, "Expected RoutingEvaluatedEvent to be emitted");
        Assertions.assertEquals("test-route-1", lastEvent.aggregateId());
        Assertions.assertNotNull(lastEvent.targetSystem());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // We check for the specific business rule violation message
        Assertions.assertTrue(
            thrownException.getMessage().contains("Business rule violation") ||
            thrownException instanceof IllegalStateException ||
            thrownException instanceof IllegalArgumentException
        );
    }
}
