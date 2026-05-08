package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup a valid state: authenticated, active, recent activity
        aggregate.hydrate(
                "teller-1",
                true,
                true,
                Instant.now().minus(10, ChronoUnit.MINUTES),
                Instant.now().minus(1, ChronoUnit.MINUTES),
                "HOME_SCREEN"
        );
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate setup, command uses this ID
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), "teller-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
        
        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.hydrate(
                null, // Not authenticated
                false,
                true,
                Instant.now(),
                Instant.now(),
                "LOGIN_SCREEN"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.hydrate(
                "teller-1",
                true,
                true,
                Instant.now().minus(60, ChronoUnit.MINUTES), // Created long ago
                Instant.now().minus(20, ChronoUnit.MINUTES), // Last activity 20 mins ago > 15 min timeout
                "HOME_SCREEN"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-409");
        aggregate.hydrate(
                "teller-1",
                true,
                true,
                Instant.now(),
                Instant.now(),
                "TRANSACTION_IN_FLIGHT" // Invalid state for termination
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify message contains context based on scenarios (optional but good practice)
    }

}
