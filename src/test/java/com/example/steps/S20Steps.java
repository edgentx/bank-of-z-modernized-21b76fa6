package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.InitiateSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a live session by executing Initiate command to set valid state
        aggregate.execute(new InitiateSessionCmd(sessionId, "TELLER-1"));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already set in the 'Given' step
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create an aggregate that is inactive (simulating no auth/init)
        sessionId = "TS-VIOLATE-AUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it remains inactive
        aggregate.markSessionInactive(); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "TS-VIOLATE-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate it so it's technically active, but force the timestamp to be old
        aggregate.execute(new InitiateSessionCmd(sessionId, "TELLER-1"));
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        sessionId = "TS-VIOLATE-NAV";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate a valid session
        aggregate.execute(new InitiateSessionCmd(sessionId, "TELLER-1"));
        // Set navigation state to something that blocks termination (e.g. mid-transaction)
        aggregate.markTransactionInProgress();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain logic throws IllegalStateException for invariants
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
