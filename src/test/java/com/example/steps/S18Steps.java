package com.example.steps;

import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import com.example.domain.uinavigation.model.TellerSessionState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in context setup or specific state injection if needed
        // For this scenario, we assume the command carries the valid ID
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in context setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-1", true, Instant.now().plusSeconds(3600));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav");
    }

    // We reuse the 'When' step for validation, but the command content will be different based on the setup
    // However, since the aggregate handles the logic, we pass valid data and let the aggregate fail internally if state is bad.
    // But the prompt implies the Command execution triggers the check. 
    // If the Aggregate holds state, we execute.
    
    // Specific When for the failure cases to pass appropriate bad data if needed, or relying on Aggregate internal state.
    // Given the constraints, I will overload the behavior or assume the 'execute' handles the check.
    
    @When("the command is executed")
    public void the_command_is_executed_invalid() {
        // The aggregate logic determines validity. 
        // For unauthenticated, we pass isAuthenticated=false
        String id = aggregate.id();
        StartSessionCmd cmd;
        
        if (id.equals("session-401")) {
             cmd = new StartSessionCmd(id, "teller-1", "terminal-1", false, Instant.now().plusSeconds(3600));
        } else if (id.equals("session-timeout")) {
             cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, Instant.now().minusSeconds(10));
        } else if (id.equals("session-nav")) {
             // State violation usually implies checking existing state
             cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, Instant.now().plusSeconds(3600));
             // Manually corrupt aggregate state for test
             aggregate.markCorrupted();
        } else {
             cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, Instant.now().plusSeconds(3600));
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
