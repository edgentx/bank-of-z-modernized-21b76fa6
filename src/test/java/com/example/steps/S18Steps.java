package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd feature.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId = "teller-001";
    private String terminalId = "term-42";
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // tellerId is already defaulted to valid in constructor, but we keep the hook for clarity
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-42";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        com.example.domain.shared.DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(sessionId, startedEvent.aggregateId());
        assertEquals(tellerId, startedEvent.tellerId());
        assertEquals(terminalId, startedEvent.terminalId());
        
        // Verify Aggregate State
        assertTrue(aggregate.isActive());
        assertTrue(aggregate.isAuthenticated());
        assertEquals("HOME", aggregate.getCurrentScreen());
    }

    // --- Scenarios for Domain Error Rejection ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    // Re-uses @When for execution, but command will differ via context or specific Given setup
    // Since Given setup here implies the *condition* for failure, we might need to override the command creation logic.
    // For simplicity in Cucumber, we can manipulate the state of the aggregate or the inputs before 'When'.
    // However, standard Cucumber usually passes data via Table or Context. Here, we can adjust the command
    // passed to 'When' if 'When' was generic. But 'When' is specific.
    // Better approach: The 'Given' sets up the AGGREGATE state or inputs, and 'When' consumes them.
    // For the specific scenario of Auth failure, we modify the 'When' call data or inject a flag.
    
    // Let's refine: The scenario says "aggregate violates...". But StartSessionCmd is an INITIATION command.
    // The invariant check is usually on the INPUT.
    // I will modify the behavior based on context in the test steps.

    @When("the StartSessionCmd command is executed with unauthenticated user")
    public void the_start_session_cmd_command_is_executed_unauthenticated() {
        // Overriding the specific command for this scenario
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, false); // isAuthenticated = false
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into an active state but with stale activity timestamp
        aggregate.setActive(true);
        // Set last activity to 20 minutes ago (assuming timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setCurrentScreen("HOME"); // Ensure context is otherwise valid
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Set screen to something invalid for a start (e.g. stuck in a transaction)
        aggregate.setActive(true); // If active, it checks this. If not active, it might not check depending on logic.
        // Logic in Aggregate: checks `currentScreen`.
        aggregate.setCurrentScreen("TRANSACTION_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the error message matches the Gherkin description loosely
        String msg = caughtException.getMessage();
        assertTrue(msg.length() > 0, "Error message should not be empty");
    }
}