package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private Aggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Simulating a fresh aggregate that has been authenticated
        this.aggregate = new TellerSessionAggregate("session-123");
        ((TellerSessionAggregate) this.aggregate).markAuthenticated(); // Helper to set valid internal state for success case
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Command construction happens in the When step, assuming validity unless specified otherwise
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Command construction happens in the When step, assuming validity unless specified otherwise
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Default state is not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-123");
        ((TellerSessionAggregate) this.aggregate).markTimedOut(); // Helper to simulate timeout invariant violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-123");
        ((TellerSessionAggregate) this.aggregate).markNavigationInvalid(); // Helper to simulate nav state violation
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            this.command = new StartSessionCmd("session-123", "teller-1", "terminal-1");
            this.resultEvents = this.aggregate.execute(this.command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass(), "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We check for IllegalStateException or IllegalArgumentException based on our implementation
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error exception, but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
