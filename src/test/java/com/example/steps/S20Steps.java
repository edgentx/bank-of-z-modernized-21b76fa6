package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "ts-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate session start to establish valid state
        aggregate.start("teller-001", "T-01", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "ts-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Aggregate is in initial state, no authentication performed.
        // However, for EndSessionCmd, we typically check if the session is active.
        // If the invariant implies "must be logged in to log out", we might need to simulate a started session
        // but this Gherkin phrase implies the PRE-CONDITION for the session itself is violated or 
        // the state of the aggregate is such that the teller isn't there.
        // Given the command is "End", a violation here usually means "Cannot end what isn't started/authenticated".
        // We keep it in unstarted state.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "ts-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate session start a long time ago
        aggregate.start("teller-001", "T-01", Instant.now().minus(Duration.ofHours(2)));
        // This aggregate is now inactive, violating the liveness invariant.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "ts-nav-error";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.start("teller-001", "T-01", Instant.now());
        // Simulate corruption or state mismatch
        aggregate.corruptNavigationState();
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception but command succeeded");
        // Usually domain errors are IllegalStateException or IllegalArgumentException, or custom DomainException
        assertTrue(capturedException instanceof IllegalStateException 
                   || capturedException instanceof IllegalArgumentException 
                   || capturedException instanceof UnknownCommandException);
    }
}