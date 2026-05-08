package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.command.NavContext;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.command.StartSessionCmd;
import com.example.domain.uimodel.event.SessionStartedEvent;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-01";
    private static final long VALID_TIMEOUT = 1800; // 30 mins

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helpers
    private NavContext validContext() {
        return new NavContext("OPERATIONAL", "MAIN_MENU");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in construction or by the command setup, here we just ensure existence
        assertNotNull(TELLER_ID);
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        assertNotNull(TERMINAL_ID);
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default valid command if not already set by specific scenario setup
        if (command == null) {
            command = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true, validContext(), Instant.now(), VALID_TIMEOUT);
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.getTellerId());
        assertEquals(TERMINAL_ID, event.getTerminalId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Create command where authenticated = false
        command = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, false, validContext(), Instant.now(), VALID_TIMEOUT);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        assertFalse(caughtException.getMessage().isBlank());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Create command with invalid timeout config (e.g., 0 or negative)
        command = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true, validContext(), Instant.now(), 0);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Create command with invalid context
        command = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true, new NavContext("", ""), Instant.now(), VALID_TIMEOUT);
    }
}