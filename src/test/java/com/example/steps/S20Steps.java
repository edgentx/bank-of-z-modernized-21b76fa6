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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state: authenticated, active, within timeout, idle
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
        aggregate.setNavigationState("IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate construction in the previous step
        // Or we can re-verify it matches if we were passing commands manually
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1))); // 1 hour ago
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15)); // Timeout after 15 mins
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "SESSION-BUSY";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState("CASH_OUT_COUNTING"); // Not IDLE
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
