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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Creates a fresh aggregate in a valid state (Authenticated, Valid Nav, Recent Activity)
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // We store this in a local variable to be used in command construction
        // The actual value is set in the 'When' step builder
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Same as above
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markUnauthenticated(); // Set state to violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markStale(); // Set lastActivity to far in the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markNavigationInvalid(); // Corrupt navigation state
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Construct command with valid data for the aggregate ID
            command = new StartSessionCmd(aggregate.id(), "teller-01", "terminal-01");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected event to be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(aggregate.id(), startedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown, but command succeeded");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException, but got " + thrownException.getClass().getSimpleName());
        
        // Ensure no events were published
        assertNull(resultEvents, "Events should not be emitted when invariant is violated");
    }
}