package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.TellerSessionStartedEvent;
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
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated(); // Ensure valid state
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When step via constructor
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When step via constructor
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Use valid data for the positive case or default
        if (cmd == null) {
            cmd = new StartSessionCmd("teller-1", "terminal-A");
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
        assertTrue(resultEvents.get(0) instanceof TellerSessionStartedEvent);
        
        TellerSessionStartedEvent event = (TellerSessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.getTellerId());
        assertEquals("terminal-A", event.getTerminalId());
    }

    // --- Negative Cases ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        this.aggregate.markUnauthenticated(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        this.aggregate.markAuthenticated(); // Valid auth
        this.aggregate.markTimedOut(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        this.aggregate.markAuthenticated(); // Valid auth
        this.aggregate.markNavigationInvalid(); // Violate invariant
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for domain exception types (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
