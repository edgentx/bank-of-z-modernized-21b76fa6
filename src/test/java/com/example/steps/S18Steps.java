package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String currentTellerId;
    private String currentTerminalId;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.currentTellerId = "teller-42";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.currentTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-401");
        // Attempt to use a null teller ID to simulate lack of auth context
        this.currentTellerId = null;
        this.currentTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Create an aggregate that effectively starts "expired"
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.currentTellerId = "teller-42";
        this.currentTerminalId = "term-01";
        
        // Force start a session in the past to simulate an expired context 
        // or simulate that the command creation logic checks timeout before execution.
        // Here we use a command with a timestamp indicating inactivity.
        // The Command will carry the timestamp of the attempt.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-error");
        this.currentTellerId = "teller-42";
        // Provide a null terminal ID to violate the operational context requirement
        this.currentTerminalId = null;
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Create command. We pass nulls for values that simulate violation scenarios set up above.
            // For the timeout scenario, we pass a timestamp that is effectively in the past relative to logic.
            StartSessionCmd cmd;
            if ("session-timeout".equals(aggregate.id())) {
                // Simulating a stale command timestamp
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, Instant.now().minus(Duration.ofHours(1)));
            } else {
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, Instant.now());
            }
            
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-01", event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
