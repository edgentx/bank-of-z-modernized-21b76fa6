package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-12345";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state defaults
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavStateValid(true);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(TellerSessionEndedEvent.class, resultEvents.get(0).getClass());
    }

    // --- Scenarios for Rejections ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "TS-AUTH-FAIL";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Violation
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // Set activity to 35 minutes ago (default timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(35)));
        aggregate.setNavStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "TS-NAV-ERR";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavStateValid(false); // Violation
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}