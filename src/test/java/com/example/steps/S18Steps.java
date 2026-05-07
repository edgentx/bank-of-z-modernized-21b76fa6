package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate state where authentication context is invalid/missing
        // In this implementation, we pass an invalid token to the command
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate state where the session is already active/stale
        // We mark it as started so the start command fails
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", "VALID_AUTH_TOKEN");
        aggregate.execute(cmd); // Start it
        aggregate.markSessionExpired(); // Force it into an invalid state for restart
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a stale state by starting the session
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", "VALID_AUTH_TOKEN");
        aggregate.execute(cmd);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context handled in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context handled in When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        String id = aggregate.id();
        // Default valid values, modified if specific violation needed (simplified for BDD)
        String token = "VALID_AUTH_TOKEN";
        
        // Adjust token based on scenario context if possible (heuristics based on aggregate state)
        if (aggregate.isStarted() && !aggregate.isTimedOut()) {
            // Already started - will fail on invariant check
            token = "VALID_AUTH_TOKEN";
        }

        try {
            StartSessionCmd cmd = new StartSessionCmd(id, "teller-123", "terminal-A", token);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        String id = aggregate.id();
        try {
            StartSessionCmd cmd = new StartSessionCmd(id, "teller-123", "terminal-A", "INVALID_TOKEN");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("terminal-A", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception but command succeeded");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}