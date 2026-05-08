package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate session;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSessionAggregate("session-123");
        // Setup: Assume a session must be authenticated and active to be valid for ending normally
        session.markAuthenticated("teller-007"); 
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate construction in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create a session but DO NOT authenticate it
        session = new TellerSessionAggregate("session-unauth-123");
        // session.markAuthenticated(...) is intentionally skipped
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSessionAggregate("session-timeout-123");
        session.markAuthenticated("teller-007");
        // Force the session into a timed-out state
        session.setTimeOutState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        session = new TellerSessionAggregate("session-nav-123");
        session.markAuthenticated("teller-007");
        // Set a restricted navigation state that prevents clean termination
        session.setRestrictedNavigationState();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id());
            resultEvents = session.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event must be instance of SessionEndedEvent");
        assertEquals("session.ended", event.type());
        
        // Verify aggregate state mutation
        assertFalse(session.isActive(), "Session should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        
        // Verify NO event was emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
    }
}
