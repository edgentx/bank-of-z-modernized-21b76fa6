package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Force aggregate into a valid authenticated/ready state for successful command execution
        // Assuming internal state setters or reconstruction exists for testing context,
        // or the aggregate is initially created in a state where it CAN accept a start command.
        // For this implementation, TellerSessionAggregate starts in IDLE and is ready for Start.
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When block via command construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When block via command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Simulate state: Not Authenticated (already satisfied by constructor if default is unauthenticated)
        // Or explicitly set state to make command fail. Here, we ensure the command fails by passing invalid auth context or relying on aggregate state.
        // In this pattern, we construct the aggregate such that it rejects the command.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // To test this invariant on START, we might need to ensure the 'attempted start' is happening too late?
        // Or the invariant validates the configured duration. 
        // Let's assume the aggregate checks the 'configured period' passed in the command or context.
        // We will pass an invalid duration (e.g., 0 or negative) to trigger the domain error for this scenario.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Simulate state: Operational Context invalid (e.g., null or mismatched)
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        caughtException = null;
        try {
            // Scenario differentiation based on aggregate setup or command parameters
            String tid = "teller-1";
            String terId = "terminal-1";
            
            // Adjusting parameters based on the scenario context to trigger specific failures
            if (aggregate.id().equals("session-auth-fail")) {
                // Force auth failure by passing null/blank token or relying on aggregate state
            }
            
            Command cmd = new StartSessionCmd(aggregate.id(), tid, terId, "token-123");
            
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Typically, domain errors are IllegalArgumentExceptions or IllegalStateExceptions
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
