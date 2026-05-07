package com.example.steps;

import com.example.domain.shared.DomainEvent;
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

    // Test State
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }
    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in context of command construction below or separate variable
    }
    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in context of command construction below
    }
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Create a valid command for the 'happy path'
            command = new StartSessionCmd("session-123", "teller-01", "term-05", "MAIN_MENU", true);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-05", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // Scenario 2: Auth Invariant
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // The violation is in the Command, not necessarily the aggregate state initially,
        // but the prompt implies the aggregate context. We will pass a bad command to a valid aggregate.
    }
    @When("the StartSessionCmd command is executed for auth check")
    public void the_StartSessionCmd_command_is_executed_for_auth_check() {
        try {
            // isAuthenticated = false
            command = new StartSessionCmd("session-auth-fail", "teller-01", "term-05", "MAIN_MENU", false);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    @Then("the command is rejected with a domain error about authentication")
    public void the_command_is_rejected_with_a_domain_error_about_auth() {
        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("authenticated"));
        assertNull(resultEvents);
    }

    // Scenario 3: Timeout Invariant
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Manually set last activity to 20 minutes ago to simulate timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }
    @When("the StartSessionCmd command is executed for timeout check")
    public void the_StartSessionCmd_command_is_executed_for_timeout_check() {
        try {
            // Valid command, but aggregate is stale
            command = new StartSessionCmd("session-timeout", "teller-01", "term-05", "MAIN_MENU", true);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    @Then("the command is rejected with a domain error about timeout")
    public void the_command_is_rejected_with_a_domain_error_about_timeout() {
        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("timeout") || thrownException.getMessage().contains("inactivity"));
        assertNull(resultEvents);
    }

    // Scenario 4: Navigation State Invariant
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }
    @When("the StartSessionCmd command is executed for navigation check")
    public void the_StartSessionCmd_command_is_executed_for_navigation_check() {
        try {
            // Context is null or wrong
            command = new StartSessionCmd("session-nav-fail", "teller-01", "term-05", null, true);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    @Then("the command is rejected with a domain error about navigation")
    public void the_command_is_rejected_with_a_domain_error_about_navigation() {
        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("Navigation state"));
        assertNull(resultEvents);
    }
}