package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception domainException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state: authenticated, active, recent activity, context valid
        aggregate.setSessionState(
            "teller-1",
            true,    // authenticated
            true,    // active
            Instant.now(), // recent activity
            true     // operational context valid
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Violation: not authenticated
        aggregate.setSessionState(
            "teller-unknown",
            false,   // NOT authenticated
            true,
            Instant.now(),
            true
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Violation: last activity was 20 minutes ago (timeout is 15)
        aggregate.setSessionState(
            "teller-1",
            true,
            true,
            Instant.now().minusSeconds(1200), // 20 mins ago
            true
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Violation: operational context invalid
        aggregate.setSessionState(
            "teller-1",
            true,
            true,
            Instant.now(),
            false  // Context invalid
        );
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        domainException = null;
        try {
            Command cmd = new EndSessionCmd(aggregate.id(), "teller-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            domainException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(domainException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted 1 event");
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertTrue(event instanceof SessionEndedEvent);
        
        SessionEndedEvent ended = (SessionEndedEvent) event;
        assertEquals("session-123", ended.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException, "Should have thrown an exception");
        assertTrue(domainException instanceof IllegalStateException);
    }
}
