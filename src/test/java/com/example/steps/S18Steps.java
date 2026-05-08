package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "teller-123";
    private String validTerminalId = "term-ABC";
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-INIT");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Implicitly handled by the default values in step definitions, but we keep context clean
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Implicitly handled
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId, Instant.now());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-INIT", event.aggregateId());
        assertEquals(validTellerId, event.tellerId());
        assertEquals(validTerminalId, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-AUTH-FAIL");
        // Violation: Provide an old authentication timestamp
        validTellerId = "teller-123"; 
        validTerminalId = "term-ABC";
        // We will modify the command execution time in the @When step for this specific scenario
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-TIMEOUT");
        // Violation: Authentication happened too long ago
        validTellerId = "teller-123";
        validTerminalId = "term-ABC";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-NAV-ERR");
        aggregate.invalidateNavigationState(); // Simulate bad state
    }

    // We need specialized When handlers or conditional logic for the negatives to pass invalid data
    // To keep it simple, we can override the behavior in specific steps, but Cucumber context isolation helps.
    
    @When("the StartSessionCmd command is executed with invalid auth context")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        // Scenario: Auth required - Passing null or very old date
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId, null); // Will fail validation in Record
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with stale auth")
    public void the_start_session_cmd_command_is_executed_with_stale_auth() {
        // Scenario: Timeout - Passing auth timestamp 20 mins ago (window is 15)
        Instant past = Instant.now().minusSeconds(1200);
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId, past);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_start_session_cmd_command_is_executed_with_invalid_nav() {
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId, Instant.now());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should be thrown");
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException ||
            capturedException instanceof UnknownCommandException,
            "Exception should be a domain error (IllegalStateException/IllegalArgumentException)");
    }
}
