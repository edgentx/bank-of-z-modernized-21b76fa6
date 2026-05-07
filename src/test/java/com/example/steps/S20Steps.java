package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        // Valid state: authenticated, active, consistent context
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.hydrate(
            "teller-123",           // authenticated teller
            Instant.now(),           // last activity (now)
            "/dashboard",           // current nav state
            true                    // consistent
        );
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is initialized in the previous step
        Assertions.assertNotNull(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Unauthenticated state
        this.aggregate.hydrate(
            null,                   // No authenticated teller
            Instant.now(),
            "/login",
            true
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Inactive state (last activity 30 minutes ago)
        this.aggregate.hydrate(
            "teller-123",
            Instant.now().minus(Duration.ofMinutes(30)),
            "/dashboard",
            true
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Inconsistent state
        this.aggregate.hydrate(
            "teller-123",
            Instant.now(),
            "/unknown-state", // Invalid/Inconsistent state
            false
        );
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(this.sessionId);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertEquals("session.ended", resultEvents.get(0).type(), "Event type mismatch");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event type mismatch");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Checking for IllegalStateException (standard domain invariant violation in our other aggregates)
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Should be an IllegalStateException");
    }
}
