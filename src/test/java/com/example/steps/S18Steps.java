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

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid command
            Command cmd = new StartSessionCmd("session-123", "teller-01", "term-01", true, true);
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
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        try {
            Command cmd = new StartSessionCmd("session-auth-fail", "teller-01", "term-01", false, true); // not authenticated
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains("authenticated") 
            || caughtException.getMessage().contains("timeout")
            || caughtException.getMessage().contains("context"));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @When("the StartSessionCmd command is executed with inactive session")
    public void the_StartSessionCmd_command_is_executed_with_inactive_session() {
        try {
            // 'isActive' false simulating the check for timeout/inactivity context
            Command cmd = new StartSessionCmd("session-timeout", "teller-01", "term-01", true, false);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with invalid context")
    public void the_StartSessionCmd_command_is_executed_with_invalid_context() {
        try {
            Command cmd = new StartSessionCmd("session-nav-fail", "teller-01", "", true, true); // Invalid terminalId context
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }
}