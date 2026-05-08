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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-12345";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup a valid active session state
        aggregate.configureForTest("TELLER-01", "MAIN_MENU", Instant.now(), true);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the previous step for simplicity, ensuring ID is not null
        assertNotNull(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "TS-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // No tellerId configured (null) implies violation
        aggregate.configureForTest(null, "LOGIN_SCREEN", Instant.now(), true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 31 minutes ago (threshold is 30)
        aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        aggregate.configureForTest("TELLER-01", "IDLE_SCREEN", Instant.now().minus(Duration.ofMinutes(31)), true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "TS-INVALID-STATE";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session that is already inactive (ended)
        aggregate.configureForTest("TELLER-01", null, Instant.now(), false);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(this.sessionId);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // In DDD with Aggregates, invariant violations usually throw IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
