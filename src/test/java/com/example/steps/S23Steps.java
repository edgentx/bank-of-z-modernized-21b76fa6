package com.example.steps;

import com.example.domain.routing.model.EvaluateRoutingCmd;
import com.example.domain.routing.model.LegacyTransactionRoute;
import com.example.domain.routing.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {
    private LegacyTransactionRoute aggregate;
    private String transactionType;
    private String payload;
    private String targetSystem;
    private int ruleVersion;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        this.transactionType = "IBMT_CREDIT_TXN";
    }

    @Given("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        this.payload = "{\"amount\": 100.00}";
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-dual-err");
        this.targetSystem = ""; // Violation: empty system implies ambiguity or dual processing attempt
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-ver-err");
        this.targetSystem = "MODERN";
        this.ruleVersion = 0; // Violation: versioning required
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            EvaluateRoutingCmd cmd = new EvaluateRoutingCmd(
                aggregate.id(),
                this.transactionType,
                this.payload,
                this.targetSystem != null ? this.targetSystem : "MODERN",
                this.ruleVersion != 0 ? this.ruleVersion : 1
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("MODERN", event.targetSystem());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}