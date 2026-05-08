package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.ui.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // State handled in command construction in @When
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // State handled in command construction in @When
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Using valid data for happy path
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "term-ABC");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("term-ABC", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Simulate violation by passing a null/invalid tellerId in the command later
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_auth_fail() {
        try {
            // Invalid command for this context
            StartSessionCmd cmd = new StartSessionCmd("session-invalid-auth", null, "term-ABC");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("tellerId required"));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // The command construction will simulate the violation via invalid flags
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_timeout_fail() {
        try {
            // Pass a boolean flag in the command that represents the violation check for timeout config
            StartSessionCmd cmd = new StartSessionCmd("session-timeout", "teller-1", "term-1", false);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav");
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_nav_fail() {
        try {
            // Pass a boolean flag that represents the violation check for nav state
            StartSessionCmd cmd = new StartSessionCmd("session-nav", "teller-1", "term-1", true, false);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }
}