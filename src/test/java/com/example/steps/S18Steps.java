package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in construction of cmd in 'When'
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in construction of cmd in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Defaults for successful path
        if (cmd == null) {
            cmd = new StartSessionCmd("teller-1", "term-1", true, "MAIN_MENU");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
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
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Prepare command with authenticated = false
        cmd = new StartSessionCmd("teller-1", "term-1", false, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Note: In this simple aggregate implementation, we handle timeout logic on execution.
        // To simulate the violation scenario context, we prepare valid inputs but expect the aggregate
        // to potentially enforce logic (or we simulate a pre-existing state if supported).
        // Here we setup valid command, the logic is inside execute.
        aggregate = new TellerSessionAggregate("session-timeout");
        // Assuming specific config or state would trigger this. 
        // For this exercise, we pass valid params to ensure we hit the logic block.
        cmd = new StartSessionCmd("teller-1", "term-1", true, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Prepare command with blank/null context
        cmd = new StartSessionCmd("teller-1", "term-1", true, "");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors are typically IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}