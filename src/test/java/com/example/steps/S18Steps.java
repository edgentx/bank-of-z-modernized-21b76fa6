package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true); // Default valid state: authenticated
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // command will be created in the When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // command will be created in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Assuming valid inputs if not specified otherwise by the context
            // Using defaults for the 'happy path'
            String tId = "teller-1";
            String termId = "term-1";
            command = new StartSessionCmd("session-123", tId, termId);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.setAuthenticated(false); // Violation: not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        aggregate.setTimeoutConfigured(false); // Violation: no timeout configured
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav");
        aggregate.setAuthenticated(true);
        // The violation here is implicit: we will send a command with invalid context (null terminalId)
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_for_rejection() {
        try {
            // Specific setup based on the violation context
            if (aggregate.id().equals("session-nav")) {
                 // Simulate bad navigation state via invalid command data
                 command = new StartSessionCmd("session-nav", "teller-1", null);
            } else {
                 // Standard command for other violations
                 command = new StartSessionCmd(aggregate.id(), "teller-1", "term-1");
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception but command succeeded");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
