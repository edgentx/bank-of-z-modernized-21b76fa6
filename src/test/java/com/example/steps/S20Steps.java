package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate an authenticated, active session
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Intentionally do NOT mark as authenticated
        assertFalse(aggregate.isAuthenticated());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated(); // Needs to be valid otherwise
        aggregate.markStale(); // Violates timeout invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.markAuthenticated();
        aggregate.lockNavigation(); // Violates navigation invariant
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The aggregate is already initialized with the ID in the Given steps.
        // This step confirms the context for the BDD flow.
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        
        SessionEndedEvent ended = (SessionEndedEvent) event;
        assertEquals("session.ended", ended.type());
        assertEquals(aggregate.id(), ended.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error exception to be thrown");
        // Depending on the specific invariant, the exception might be IllegalStateException
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof UnknownCommandException ||
                   caughtException instanceof IllegalArgumentException);
    }
}
