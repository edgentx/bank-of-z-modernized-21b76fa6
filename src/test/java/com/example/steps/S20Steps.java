package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermession.model.EndSessionCmd;
import com.example.domain.tellermession.model.SessionEndedEvent;
import com.example.domain.tellermession.model.TellerSessionAggregate;
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
    private String sessionId;
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Standard active session: authenticated, active, valid context
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // We simulate the state by forcing the fields necessary for the test
        // In a real scenario, this would be done by hydrating from events, but for unit steps we assume valid state.
        aggregate.hydrateForTest(true, Instant.now().minusSeconds(60), true, true); 
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        assertEquals("session.ended", resultingEvents.get(0).type());
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Not authenticated
        aggregate.hydrateForTest(false, Instant.now().minusSeconds(60), true, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Last activity was 2 hours ago (assuming timeout is 30 mins)
        aggregate.hydrateForTest(true, Instant.now().minusSeconds(7200), true, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        sessionId = "session-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Invalid navigation state (e.g. stuck in a transaction that doesn't exist)
        aggregate.hydrateForTest(true, Instant.now().minusSeconds(60), true, false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Checking for common domain error types (IllegalStateException, IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
