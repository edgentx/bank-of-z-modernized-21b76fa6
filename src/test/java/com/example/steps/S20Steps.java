package com.example.steps;

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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private static final String TEST_SESSION_ID = "session-123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Setup a valid, authenticated, active session
        Instant activeTime = Instant.now();
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, true, activeTime, true);
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Authenticated = false
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, false, Instant.now(), true);
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Last activity was 20 minutes ago (Timeout is 15)
        Instant expiredTime = Instant.now().minus(Duration.ofMinutes(20));
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, true, expiredTime, true);
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Navigation is unstable
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, true, Instant.now(), false);
        caughtException = null;
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate is already initialized with the ID, ensuring consistency.
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(TEST_SESSION_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // The domain error is represented by IllegalStateException in this implementation
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
