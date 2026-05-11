package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String testTellerId;
    private String testTerminalId;
    private List<DomainEvent> resultEvents;
    private RuntimeException thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.testTellerId = "teller-42";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.testTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Simulate state where authentication fails or is missing.
        // For this scenario, we pass null for tellerId in the command to trigger logic.
        // In a real aggregate, this might be an internal state check against a token.
        aggregate = new TellerSessionAggregate("session-unauth");
        this.testTellerId = null; 
        this.testTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Simulate state where the session is already active/expired.
        aggregate = new TellerSessionAggregate("session-expired");
        this.testTellerId = "teller-42";
        this.testTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        // Simulate invalid terminal ID (e.g., null or blank)
        aggregate = new TellerSessionAggregate("session-bad-nav");
        this.testTellerId = "teller-42";
        this.testTerminalId = null; 
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(testTellerId, testTerminalId, Instant.now());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (RuntimeException e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}