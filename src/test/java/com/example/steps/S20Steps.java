package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a session in a valid, active state
        String sessionId = "sess-123";
        String tellerId = "teller-01";
        // Assuming an active session implies authenticated and non-timed out
        aggregate = new TellerSessionAggregate(sessionId);
        // We hydrate it via test accessor or construction to be 'active'
        // For this test, we assume new aggregates are active if constructed with ID,
        // but we simulate the 'authenticated' state by not setting it to timed out.
        aggregate.__internal_setAuthenticated(true);
        aggregate.__internal_setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "sess-invalid-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internal_setAuthenticated(false);
        aggregate.__internal_setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internal_setAuthenticated(true);
        // Set last activity to 2 hours ago (assuming timeout is < 2 hours)
        aggregate.__internal_setLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internal_setAuthenticated(true);
        aggregate.__internal_setLastActivity(Instant.now());
        // Set a state that implies an active transaction or locked screen
        aggregate.__internal_setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by aggregate construction in previous steps
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents, "Expected no events to be emitted");
    }
}
