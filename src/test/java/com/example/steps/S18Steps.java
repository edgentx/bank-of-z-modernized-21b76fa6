package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate authenticated state and valid navigation for success scenario
        // (In a real app, these might be set via constructor or past events)
        // For this BDD stub, we assume the aggregate constructor creates a valid, empty state.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Valid teller ID setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Valid terminal ID setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Create command with valid data
        cmd = new StartSessionCmd("session-123", "teller-001", "terminal-101");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-101", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-violated-auth");
        aggregate.markAsUnauthenticated(); // Simulated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-violated-timeout");
        aggregate.markAsTimedOut(); // Simulated state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-violated-nav");
        aggregate.markNavigationInvalid(); // Simulated state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents, "No events should be emitted on rejection");
    }
}
