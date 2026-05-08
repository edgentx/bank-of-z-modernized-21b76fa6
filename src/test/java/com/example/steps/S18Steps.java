package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-18: Cucumber Steps for TellerSession Aggregate.
 * Tests the StartSessionCmd logic and invariant enforcement.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // --- Shared Helper ---
    private void executeCommand(StartSessionCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Scenario 1: Success ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Teller ID passed in command during When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Terminal ID passed in command during When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Assume default valid params for the positive path
        StartSessionCmd cmd = new StartSessionCmd(
                "user-01",
                "term-01",
                true,   // authenticated
                true,   // terminal active
                60000,  // timeout 60s
                "HOME"  // valid context
        );
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not throw exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("user-01", event.tellerId());
        assertEquals("term-01", event.terminalId());
    }

    // --- Scenario 2: Auth Required ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    // Reuse When/Then from positive case by context injection, or explicit steps
    // We use an explicit When for clarity here to pass bad data
    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        StartSessionCmd cmd = new StartSessionCmd(
                "user-01", "term-01",
                false, // NOT authenticated
                true, 60000, "HOME"
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Should be IllegalStateException");
        assertTrue(caughtException.getMessage().contains("authenticated"), "Error message should mention authentication");
    }

    // --- Scenario 3: Timeout Required ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_with_invalid_timeout() {
        StartSessionCmd cmd = new StartSessionCmd(
                "user-01", "term-01",
                true, true,
                100, // 100ms timeout (too low)
                "HOME"
        );
        executeCommand(cmd);
    }

    // Then reused from Scenario 2

    // --- Scenario 4: Navigation State ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_StartSessionCmd_command_is_executed_with_invalid_navigation_state() {
        StartSessionCmd cmd = new StartSessionCmd(
                "user-01", "term-01",
                true, true, 60000,
                "TRANS_FLOW_2" // Invalid context
        );
        executeCommand(cmd);
    }

    // Then reused from Scenario 2
}
