package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    private static final String VALID_SESSION_ID = "SESSION-123";
    private static final String VALID_TELLER_ID = "TELLER-42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Setup valid state for a session that can be ended
        aggregate.markAuthenticated(VALID_TELLER_ID);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Normally this comes from the request, here we construct the Command object
        command = new EndSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should be thrown for invariant violation");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertNull(resultEvents, "No events should be emitted on failure");
    }

    // --- Negative Scenarios Setup ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Intentionally do NOT call markAuthenticated
        command = new EndSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(VALID_TELLER_ID);
        // Force timeout
        aggregate.markTimedOut();
        command = new EndSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(VALID_TELLER_ID);
        // Corrupt navigation state
        aggregate.corruptNavigationState();
        command = new EndSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID);
    }
}