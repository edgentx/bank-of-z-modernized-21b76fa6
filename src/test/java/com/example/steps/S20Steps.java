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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.initializeSession("teller-456"); // Setup valid state
        aggregate.updateLastActivity(Instant.now());
        aggregate.setNavigationState("IDLE");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate ID used in a_valid_teller_session_aggregate
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-401");
        // Not calling initializeSession -> Authenticated = False
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_is_timed_out() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.initializeSession("teller-456");
        aggregate.setNavigationState("IDLE");
        // Simulate timeout by setting last activity to ancient history
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_with_invalid_nav_state() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.initializeSession("teller-456");
        aggregate.updateLastActivity(Instant.now());
        // Simulate invalid state, e.g., "TRANSACTING" when it should be "IDLE" to end, or just a garbage state
        aggregate.setNavigationState("TRANSACTING"); // Assume we can only end from IDLE
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
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
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Invariants throw IllegalStateException (Domain Error)
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}
