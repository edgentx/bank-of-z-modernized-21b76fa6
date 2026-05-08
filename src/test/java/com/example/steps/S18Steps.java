package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Scenario 1: Successfully execute StartSessionCmd ---
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup defaults for success
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("LOGIN"); // Valid context
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in command construction, but we ensure the command uses valid IDs
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            command = new StartSessionCmd("session-123", "teller-42", "term-T01");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-T01", event.terminalId());
    }

    // --- Scenario 2: Auth Required ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setNavigationState("LOGIN");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // --- Scenario 3: Timeout Invariant ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("LOGIN");
        
        // Force an expired state
        aggregate.setActive(true); 
        aggregate.setSessionTimeout(Duration.ofMinutes(10));
        aggregate.markInactiveForDuration(Duration.ofMinutes(15)); // Exceeds timeout
    }

    // Reuse When/Then from above (generic matcher)

    // --- Scenario 4: Navigation State Invariant ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setAuthenticated(true);
        // Set an invalid navigation state to trigger the invariant check
        aggregate.setNavigationState("UNKNOWN");
    }
}