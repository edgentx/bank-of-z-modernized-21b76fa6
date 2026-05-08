package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd feature.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure clean state
        aggregate.setNavigationState("HOME");
        aggregate.setAuthenticated(true);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Command will be constructed in the 'When' block using specific data, or we store data here.
        // For simplicity, we assume the 'When' step constructs it, but we can store expectations.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        command = new StartSessionCmd("session-123", "teller-001", "terminal-01");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-01", event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-violation-auth");
        aggregate.setAuthenticated(false); // Simulate unauthenticated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-violation-timeout");
        // Simulate an old session that should have timed out
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-violation-nav");
        // Set a context that doesn't make sense for starting a session (e.g. inside a transaction)
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // Verify it is a domain specific error (IllegalStateException or IllegalArgumentException)
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception should be a domain logic error (IllegalState or IllegalArgument)"
        );
    }
}
