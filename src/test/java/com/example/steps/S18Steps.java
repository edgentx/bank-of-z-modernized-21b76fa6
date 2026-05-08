package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Default to a valid state (Authenticated, Idle, Timely)
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Violation: isAuthenticated is false by default
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setActive(true);
        // Simulate an old activity timestamp to trigger timeout check on re-use/start
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1000)); // 16+ minutes ago
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        // Violation: State is not IDLE (e.g., stuck in a transaction screen)
        aggregate.setNavigationState("TXN_DEPOSIT_IN_PROGRESS");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // We'll build the command in the 'When' step, just ensuring validity here implicitly
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // We'll build the command in the 'When' step, just ensuring validity here implicitly
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        command = new StartSessionCmd("session-123", "teller-001", "terminal-101");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-101", event.terminalId());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In a robust DDD app, we might throw a custom DomainError, but for this level
        // standard IllegalStateException (as used in the aggregate) is sufficient proof of rejection.
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException || 
                   capturedException instanceof UnknownCommandException);
    }
}
