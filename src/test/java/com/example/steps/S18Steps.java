package com.example.steps;

import com.example.domain.shared.Command;
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
    private DomainEvent resultEvent;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state (Authenticated, Correct Context)
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Intent is captured in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Intent is captured in the 'When' step construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "TS-NO-AUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // isAuthenticated is false by default
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "TS-STALE";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        // Simulate a stale session that prevents restart or violates invariant logic
        aggregate.setStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "TS-BAD-STATE";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set a state that implies the teller is already busy
        aggregate.setNavigationState("IN_TRANSACTION");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), "TELLER-1", "TERM-01");
            List<DomainEvent> events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertTrue(resultEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) resultEvent;
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain exception");
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException,
            "Expected IllegalStateException or UnknownCommandException, but got: " + caughtException.getClass().getSimpleName()
        );
        assertTrue(caughtException.getMessage() != null && !caughtException.getMessage().isBlank());
    }
}