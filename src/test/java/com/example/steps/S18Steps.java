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
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Set up valid state defaults
        aggregate.markAuthenticated();
        aggregate.markValidContext();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do NOT mark authenticated -> violates invariant
        aggregate.markValidContext();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        aggregate.markValidContext();
        aggregate.markTimedOut(); // violates invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context-error");
        aggregate.markAuthenticated();
        aggregate.markInvalidContext(); // violates invariant
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "terminal-A";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), this.tellerId, this.terminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
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
        assertEquals("session.started", event.type());
        assertEquals(this.tellerId, event.tellerId());
        assertEquals(this.terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertNull(resultEvents, "Events should be null on command rejection");
    }
}
