package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.model.TellerSessionState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSession("session-1");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // In a real setup, this would configure the command context
        // For this unit test level, we construct the command with this value directly in the 'When'
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid command for the success scenario
            Command cmd = new StartSessionCmd("session-1", "teller-123", "terminal-456", true);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.started", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSession("session-auth-fail");
        // The violation is implicit in the command we will send (authenticated = false)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // Note: This scenario implies business logic enforcement.
        // Since we are starting a session, we enforce that the client acknowledges the timeout config.
        this.aggregate = new TellerSession("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSession("session-nav-fail");
    }

    // We use a separate When for error scenarios or reuse via dynamic context, 
    // but Cucumber prefers unique bindings. We'll add specific ones for clarity or overload context.
    // For simplicity in this implementation, we will capture the specific error types based on the Givens above
    // by setting internal state flags or using specific command executions.
    
    // To keep it simple and readable:
    @When("the StartSessionCmd command is executed with invalid context")
    public void the_StartSessionCmd_command_is_executed_with_invalid_context() {
        try {
            // We simulate the violations by creating commands that trigger them
            Command cmd;
            if (aggregate.getId().contains("auth-fail")) {
                cmd = new StartSessionCmd(aggregate.getId(), "teller-123", "term-123", false); // Not authenticated
            } else if (aggregate.getId().contains("timeout-fail")) {
                cmd = new StartSessionCmd(aggregate.getId(), "teller-123", "term-123", true, 0); // 0 timeout invalid
            } else { // nav-fail
                cmd = new StartSessionCmd(aggregate.getId(), "teller-123", "term-123", true, 1800, null); // null context
            }
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
