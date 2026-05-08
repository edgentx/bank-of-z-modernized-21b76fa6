package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private Exception caughtException;
    private String lastEventType;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Teller ID provided in the command construction in the When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Terminal ID provided in the command construction in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        command = new StartSessionCmd("session-123", "teller-42", "term-01");
        try {
            var events = aggregate.execute(command);
            if (!events.isEmpty()) {
                lastEventType = events.get(0).type();
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(lastEventType, "An event should have been emitted");
        Assertions.assertEquals("teller.session.started", lastEventType);
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Simulate a state where the teller is not authenticated (e.g. tracking state in aggregate)
        // For this test, we rely on the Aggregate logic to enforce.
        // Since StartSessionCmd itself is the trigger, the violation implies the command data might be invalid
        // or the aggregate state disallows it. Here we construct an invalid command scenario.
        command = new StartSessionCmd("session-unauth", "", "term-01"); // Blank teller ID
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Hypothetical: trying to start a session that is already active or timed out? 
        // The prompt implies the aggregate state violates the invariant. 
        // We simulate this by trying to start a session when one is already active.
        
        // First execution succeeds
        aggregate.execute(new StartSessionCmd("session-timeout", "teller-1", "term-1"));
        
        // Second execution (Scenario Context) is prepared in the When block
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav");
        // We simulate a violation by passing an invalid terminal ID (e.g. offline or wrong context)
        // In a real system, this might check a registry. Here we check format/presence.
        command = new StartSessionCmd("session-nav", "teller-1", null); // Null terminal ID
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_failure() {
        if (command == null) {
             // Specific setup for the timeout case where command wasn't set in Given
             if (aggregate.id().equals("session-timeout")) {
                 command = new StartSessionCmd("session-timeout", "teller-2", "term-1");
             }
        }
        try {
            aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException"
        );
    }
}
