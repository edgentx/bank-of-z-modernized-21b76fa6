package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private StartSessionCmd cmd;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAsAuthenticated(); // Ensure pre-condition for validity
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the When step construction or setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the When step construction or setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsAuthenticated();
        aggregate.markAsStale(); // Force the timeout condition
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAsAuthenticated();
        // The violation here is simulated by passing a bad command in the 'When' step context
        // or setting internal state. Since the command carries the terminalId, we will pass a null/blank command later.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command data
        String tId = "teller-01";
        String termId = "terminal-01";
        
        // Detect specific scenario context to adjust command data
        // Note: In Cucumber, we usually rely on scenario context, but here we inspect the aggregate state 
        // or just assume valid defaults unless the specific violation scenario is active.
        
        if (aggregate.id().equals("session-nav-error")) {
            termId = ""; // Blank terminal ID violates navigation context accuracy
        }

        cmd = new StartSessionCmd(aggregate.id(), tId, termId);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session-123", sse.aggregateId());
        assertEquals("teller-01", sse.tellerId());
        assertEquals("terminal-01", sse.terminalId());
        assertEquals("session.started", sse.type());
        
        // Verify aggregate state changed
        // (Accessing private fields would need getters, which are on the AggregateRoot but not specific state)
        // We assume the event emission is sufficient proof of state change in this bounded context.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check specific message content based on the scenario
        assertTrue(
            caughtException.getMessage().contains("authenticated") ||
            caughtException.getMessage().contains("timeout") ||
            caughtException.getMessage().contains("TerminalId") ||
            caughtException.getMessage().contains("inactivity"),
            "Expected exception message to match domain invariant violation. Got: " + caughtException.getMessage()
        );
    }
}
