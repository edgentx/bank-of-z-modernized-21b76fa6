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
    private EndSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implied by the aggregate construction
        // We construct the command with a matching ID
        command = new EndSessionCmd("session-123", "teller-1", Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setState(false, Instant.now(), Duration.ofMinutes(15), true);
        command = new EndSessionCmd("session-auth-fail", "teller-1", Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Set last activity to 20 minutes ago (threshold is 15)
        aggregate.setState(true, Instant.now().minus(Duration.ofMinutes(20)), Duration.ofMinutes(15), true);
        command = new EndSessionCmd("session-timeout-fail", "teller-1", Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setState(true, Instant.now(), Duration.ofMinutes(15), false);
        command = new EndSessionCmd("session-nav-fail", "teller-1", Duration.ofMinutes(15));
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "One event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", resultingEvents.get(0).type());
        assertTrue(aggregate.isEnded(), "Aggregate state should be ended");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}