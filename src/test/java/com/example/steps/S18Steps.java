package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid base aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate("session-123");
    }

    // Helper for a valid command
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd("session-123", "teller-1", "term-A", true, Instant.now());
    }

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = createValidAggregate();
        // Reset exception holder
        thrownException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in command construction, this step is declarative in Gherkin
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = createValidCommand();
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // Scenario: StartSessionCmd rejected — Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = createValidAggregate();
        // The invariant violation is in the Command's authentication flag for this specific logic flow
        // or in the aggregate state if we were resuming. Since we are starting, we test via command input.
        thrownException = null;
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "term-A", false, Instant.now());
        executeCommand(cmd);
    }

    // Scenario: StartSessionCmd rejected — Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = createValidAggregate();
        // Set last activity to 20 minutes ago to simulate timeout
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
        thrownException = null;
    }

    // Scenario: StartSessionCmd rejected — Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = createValidAggregate();
        // Set state to something invalid for starting (e.g., LOCKED or TRANSACTION_PENDING)
        aggregate.setNavigationState(TellerSessionAggregate.NavigationState.LOCKED);
        thrownException = null;
    }

    // Shared Then steps for rejection scenarios
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the specific error message based on the scenario context
        String message = thrownException.getMessage();
        assertTrue(
            message.contains("authenticated") || 
            message.contains("timeout") || 
            message.contains("Navigation state"),
            "Error message should match the violated invariant. Got: " + message
        );
        
        // Ensure no events were emitted on failure
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }

    // Helper execution method
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
