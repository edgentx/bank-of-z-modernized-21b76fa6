package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set to valid state for happy path
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentNavigationState("HOME");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate constructor in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setActive(true); // Active but not authenticated
        aggregate.setAuthenticated(false);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Set activity to 35 minutes ago (timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(35 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "session-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentNavigationState("INVALID_CONTEXT");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof TellerSessionEndedEvent, "Expected TellerSessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
