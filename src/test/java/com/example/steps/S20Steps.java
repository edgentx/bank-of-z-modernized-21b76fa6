package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a valid, active, authenticated session
        aggregate.markActiveAndAuthenticated();
        aggregate.setCurrentState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the setup of the aggregate above, implicit validation in command execution
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally not calling markActiveAndAuthenticated, so 'authenticated' is false.
        // We ensure it is active conceptually but missing auth token.
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markActiveAndAuthenticated();
        // Set activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
        aggregate.setCurrentState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "sess-busy";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markActiveAndAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set a state that implies an unfinished transaction
        aggregate.setCurrentState("TRANSACTION_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }

}