package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context stored in scenario state, applied in command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context stored in scenario state, applied in command execution
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default valid command data
        String tellerId = "teller-123";
        String terminalId = "term-A";
        Instant authenticatedAt = Instant.now();
        StartSessionCmd cmd = new StartSessionCmd("session-1", tellerId, terminalId, authenticatedAt);

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("session.started", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Simulate lack of authentication by passing null
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Logic handled in command validation by simulating stale auth
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        // Create an aggregate that is already started to violate init state
        StartSessionCmd cmd = new StartSessionCmd("session-4", "teller-1", "term-1", Instant.now());
        aggregate.execute(cmd);
    }

    @When("the StartSessionCmd command is executed")
    public void execute_command_with_context() {
        // Determining command payload based on the "Given" context
        // Since Cucumber context is shared, we inspect the aggregate state or hypothetical flags.
        // For simplicity in this unit-test style steps:
        
        StartSessionCmd cmd;
        
        // If aggregate is already active (Scenario 4), sending another start command
        if (!aggregate.uncommittedEvents().isEmpty()) {
            // Scenario 4: Already started
            cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", Instant.now());
        }
        // If aggregate is invalid auth (Scenario 2)
        else if (aggregate.id().equals("session-2")) {
            cmd = new StartSessionCmd(aggregate.id(), null, "term-1", null); // Violation
        }
        // If aggregate is timeout (Scenario 3)
        else if (aggregate.id().equals("session-3")) {
            // Simulate an auth timestamp that is too old
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", past);
        }
        else {
             // Should not happen given the Givens, but fallback
            cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", Instant.now());
        }

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}