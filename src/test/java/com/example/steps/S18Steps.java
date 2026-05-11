package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private String validTellerId = "TELLER_123";
    private String validTerminalId = "TERM_01";
    private String validNavState = "CONTEXT_MAIN_MENU";
    private long validTimeout = 1800000; // 30 mins

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_001");
        caughtException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in When step via constants
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in When step via constants
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            aggregate.id(),
            validTellerId,
            validTerminalId,
            true, // Authenticated by default unless overridden by Given violation
            validTimeout,
            validNavState
        );
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
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("teller.session.started", event.type());
        assertEquals("TELLER_123", event.tellerId());
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
        // Simulate unauthenticated state
        // Note: In this design, auth status is passed in Command, not stored in Aggregate state before execution.
    }

    // We override the execution context for violation scenarios
    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_invalid_auth() {
        StartSessionCmd cmd = new StartSessionCmd(
            aggregate.id(), validTellerId, validTerminalId,
            false, // Not authenticated
            validTimeout, validNavState
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT_FAIL");
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_invalid_timeout() {
        StartSessionCmd cmd = new StartSessionCmd(
            aggregate.id(), validTellerId, validTerminalId,
            true, -100, // Invalid timeout
            validNavState
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("SESSION_NAV_FAIL");
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void the_StartSessionCmd_command_is_executed_invalid_navigation() {
        StartSessionCmd cmd = new StartSessionCmd(
            aggregate.id(), validTellerId, validTerminalId,
            true, validTimeout, "" // Blank nav state
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertFalse(caughtException.getMessage().isBlank(), "Exception should have a message");
        System.out.println("Caught expected domain error: " + caughtException.getMessage());
    }
}