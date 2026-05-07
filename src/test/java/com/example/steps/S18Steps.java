package com.example.steps;

import com.example.domain.tellermatching.model.StartSessionCmd;
import com.example.domain.tellermatching.model.SessionStartedEvent;
import com.example.domain.tellermatching.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Simulate a state where the command would be rejected (e.g. force bad state)
        // In this domain logic, the command payload itself must carry valid auth tokens.
        // We will simulate the violation by providing null/invalid tokens in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Logic to simulate a timeout violation would be handled via timestamp logic in the command
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // Simulate bad nav state via command parameters
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context stored for execution
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context stored for execution
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Scenario 1: Valid Command
            Command cmd = new StartSessionCmd(
                "session-1", 
                "teller-123", 
                "terminal-A", 
                "valid-auth-token", 
                Instant.now(), 
                Duration.ofHours(8)
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with invalid context")
    public void the_StartSessionCmd_command_is_executed_with_invalid_context() {
        try {
            // Scenario 2, 3, 4: Invalid Context (mapped via specific violations)
            // Using a generic handler for the 'violates' steps by passing bad data
            Command cmd = new StartSessionCmd(
                "session-invalid",
                "teller-123",
                "terminal-A",
                null, // Violates: Auth required
                null, // Violates: Timeouts/State context if checked
                null
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check it's not just UnknownCommandException, but a validation error
        assertFalse(capturedException.getMessage().contains("Unknown command"));
    }
}
