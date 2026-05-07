package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: Implement StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the When step via the command constructor, 
        // but we can set default expectations here if needed.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the When step.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default command for happy path
        if (command == null) {
            command = new StartSessionCmd("session-123", "teller-01", "terminal-A", true, "HOME");
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
        assertEquals("HOME", event.navigationState());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-999");
        this.command = new StartSessionCmd("session-999", "teller-01", "terminal-A", false, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // This invariant implies the session has been active too long.
        // Since we can only command 'StartSession', we simulate this by creating a session
        // that is already ACTIVE (meaning it was started previously and possibly timed out).
        // The StartSessionCmd should fail because the aggregate state is not IDLE.
        this.aggregate = new TellerSessionAggregate("session-timeout");
        // We manually manipulate state for the test scenario setup to represent an invalid context for Start
        // In a real app, this would be loaded from a repo as ACTIVE.
        aggregate.execute(new StartSessionCmd("session-timeout", "teller-old", "term-old", true, "HOME"));
        // Now we try to start it again (violation of lifecycle logic)
        this.command = new StartSessionCmd("session-timeout", "teller-new", "term-new", true, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        this.aggregate = new TellerSessionAggregate("session-nav-error");
        // Navigation context is invalid (e.g., deep in a transaction menu instead of HOME)
        this.command = new StartSessionCmd("session-nav-error", "teller-01", "terminal-A", true, "TRANSACTION_MENU");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's an explicit domain rejection (IllegalArgument or IllegalState)
        assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException,
            "Expected domain exception, got: " + capturedException.getClass().getSimpleName()
        );
    }
}
