package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String providedTellerId;
    private String providedTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test State Flags
    private boolean isAuthenticated = true;
    private boolean isActive = false;
    private Instant lastActivityOverride = null;
    private String expectedContext = "HOME";
    private String aggregateContext = "HOME";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Default state: valid, ready to start
        isAuthenticated = true;
        isActive = false;
        expectedContext = "HOME";
        aggregateContext = "HOME";
        lastActivityOverride = null;
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        providedTellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        providedTerminalId = "term-42";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Apply test state overrides to the aggregate if necessary before executing
            if (lastActivityOverride != null) {
                aggregate.setLastActivityAt(lastActivityOverride);
            }
            if (isActive) {
                aggregate.setActive(true);
                // Using reflection or a setter to force state for 'Given' scenario setup 
                // is acceptable in BDD infrastructure to test 'reject' scenarios.
                // Here we re-instantiate the aggregate with specific state if needed, 
                // but for simplicity we manipulate the public setters/test hooks.
            }
            
            // Sync the internal context if we are simulating an existing active session
            if (isActive) {
                // (In a real repo, we'd load this state. Here we mock it.)
            }

            StartSessionCmd cmd = new StartSessionCmd(
                "session-123",
                providedTellerId,
                providedTerminalId,
                isAuthenticated,
                isActive, // Indicates if we expect it to be already active (for context checks)
                expectedContext
            );

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }

    // 
    // NEGATIVE SCENARIO SETUP
    //

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        isAuthenticated = false; // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        // Force the aggregate to look active but timed out
        isActive = true; 
        // Set last activity to 20 minutes ago (Timeout is 15)
        lastActivityOverride = Instant.now().minusSeconds(20 * 60);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        
        // Simulate an active session in context 'HOME'
        isActive = true;
        
        // Command will request context 'ACCOUNTS', causing mismatch
        expectedContext = "ACCOUNTS";
    }
}
