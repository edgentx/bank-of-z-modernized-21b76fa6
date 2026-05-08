package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private static final String SESSION_ID = "sess-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-42";

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a fresh aggregate
    private void createFreshAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Default: authenticated, valid state, fresh timestamp to ensure positive tests pass unless modified
        aggregate.markAuthenticated(true);
        aggregate.setStatus(TellerSessionAggregate.Status.NONE);
        aggregate.setNavigationState("INIT");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        createFreshAggregate();
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // TellerId is provided in the Command object in the 'When' step
        // We just ensure the test data is valid
        assertNotNull(TELLER_ID);
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        assertNotNull(TERMINAL_ID);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        createFreshAggregate();
        // Force unauthenticated state
        aggregate.markAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        createFreshAggregate();
        // Set status to Active but with a very old timestamp
        aggregate.setStatus(TellerSessionAggregate.Status.ACTIVE);
        // 20 minutes ago (assuming 15 min timeout)
        Instant past = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.setLastActivityAt(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        createFreshAggregate();
        // Set a state that implies we are busy (e.g. deep in a menu)
        aggregate.setNavigationState("CUSTOMER_DETAIL_VIEW");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(TELLER_ID, TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.tellerId());
        assertEquals(TERMINAL_ID, event.terminalId());
        assertEquals("session.started", event.type());
        
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown, but none was");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException, got " + thrownException.getClass().getSimpleName());
        
        // Ensure no events were committed if rejected
        assertNull(resultEvents, "Expected no events to be emitted on rejection");
    }
}