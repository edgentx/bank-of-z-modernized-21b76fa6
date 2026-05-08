package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate a valid, active, authenticated session
        aggregate.hydrateForTest(
            "teller-001",
            Instant.now().minusSeconds(300), // started 5 mins ago
            Instant.now().minusSeconds(10),  // last activity 10 secs ago
            "IDLE"
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is part of the aggregate constructor in this simplified model,
        // or passed via the Command. For this test, we ensure the aggregate ID matches the intent.
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Hydrate with active = true but authenticated = false
        aggregate.hydrateForTest(
            null, // No teller ID
            Instant.now(),
            Instant.now(),
            "IDLE"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Hydrate with last activity far in the past (exceeding 15 min timeout)
        aggregate.hydrateForTest(
            "teller-001",
            Instant.now().minus(Duration.ofHours(1)),
            Instant.now().minus(Duration.ofMinutes(20)),
            "IDLE"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-busy");
        // Hydrate with a context that forbids termination
        aggregate.hydrateForTest(
            "teller-001",
            Instant.now(),
            Instant.now(),
            "TRANSACTION_IN_PROGRESS"
        );
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertFalse(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Ensure no events were emitted
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }
}
