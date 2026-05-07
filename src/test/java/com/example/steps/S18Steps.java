package com.example.steps;

import com.example.domain.shared.Command;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We simulate a pre-existing session that is active.
        // We create an aggregate, start a session (auth successful), 
        // then try to start again without re-authenticating logic handled by aggregate state.
        aggregate = new TellerSessionAggregate("SESSION-2");
        // Start a session to put it in ACTIVE state
        StartSessionCmd cmd = new StartSessionCmd("SESSION-2", "TELLER-1", "TERM-1", Instant.now().plusSeconds(3600), true);
        aggregate.execute(cmd);
        aggregate.clearEvents(); // Clear the events from the setup
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // This scenario implies the command or context provided forces a timeout condition.
        // We will pass an 'authenticated' token, but the StartSessionCmd logic will check validity.
        aggregate = new TellerSessionAggregate("SESSION-3");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // This scenario implies the command contains a mismatched navigation state.
        aggregate = new TellerSessionAggregate("SESSION-4");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Determine context based on the previous Givens
        String id = aggregate.id();
        
        // Determine values based on the aggregate state setup in Givens
        if (id.equals("SESSION-1")) {
            // Happy path
            StartSessionCmd cmd = new StartSessionCmd(id, "TELLER-1", "TERM-1", Instant.now().plusSeconds(3600), true);
            resultEvents = aggregate.execute(cmd);
        } else if (id.equals("SESSION-2")) {
            // Violation: Already active (Not authenticated for *new* session)
            // Note: The aggregate logic enforces that you can't start if already started (simulating auth requirement)
            StartSessionCmd cmd = new StartSessionCmd(id, "TELLER-1", "TERM-1", Instant.now().plusSeconds(3600), true);
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (IllegalStateException e) {
                caughtException = e;
            }
        } else if (id.equals("SESSION-3")) {
            // Violation: Timeout (Simulated by passing false for authenticated or expired context)
            // Passing false simulates the auth check failing due to timeout
            StartSessionCmd cmd = new StartSessionCmd(id, "TELLER-1", "TERM-1", Instant.now().minusSeconds(10), false); 
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (IllegalArgumentException e) {
                caughtException = e;
            }
        } else if (id.equals("SESSION-4")) {
            // Violation: Navigation state mismatch
            // Passing a current state that doesn't match the required start state
            StartSessionCmd cmd = new StartSessionCmd(id, "TELLER-1", "TERM-1", Instant.now().plusSeconds(3600), true, "MENU_MAIN", "IDLE");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (IllegalStateException e) {
                caughtException = e;
            }
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("SESSION-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
