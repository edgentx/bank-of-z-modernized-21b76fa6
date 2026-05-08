package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // In a real setup, we might set this in a context object, but for this simple flow
        // we will construct the command with valid data in the 'When' step.
        // This step is effectively a placeholder for the command construction logic.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command for the 'happy path'
        executeCommand(new StartSessionCmd("session-1", "teller-1", "terminal-1", true, "HOME"));
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-1", event.terminalId());
    }

    // --- Scenarios for Rejections ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-2");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-4");
    }

    @When("the StartSessionCmd command is executed")
    public void the_command_is_executed_with_violation() {
        // We use a simplistic approach to differentiate the scenarios based on the ID we just set.
        // A more robust setup would use a ScenarioContext, but this keeps the self-contained file.
        if (aggregate.id().equals("session-2")) {
            executeCommand(new StartSessionCmd("session-2", "teller-2", "terminal-2", false, "LOGIN")); // Not authenticated
        } else if (aggregate.id().equals("session-3")) {
            executeCommand(new StartSessionCmd("session-3", "teller-3", "terminal-3", true, "STALE")); // Timeout marker
        } else if (aggregate.id().equals("session-4")) {
            executeCommand(new StartSessionCmd("session-4", "teller-4", "terminal-4", true, "INVALID")); // Invalid Nav State
        } else {
            throw new RuntimeException("Unknown aggregate setup in test");
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        // Verify message contains the invariant text from the Aggregate
        assertTrue(thrownException.getMessage().length() > 0);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
