package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainException> errors;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // In a real flow, this might be captured in a context object, here it's implicit in the command
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // In a real flow, this might be captured in a context object
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We create an aggregate but we will issue the command with isAuthenticated=false
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Scenario implies we are trying to start a session that is already stale or invalid
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // We assume the violations are passed as flags/parameters in the command for testing purposes
            // or we inspect the specific scenario context. Here we use parameters on the Cmd to simulate the violation state.
            
            String id = aggregate.id();
            boolean isAuthenticated = true; // default
            boolean isTimedOut = false;
            boolean isNavStateInvalid = false;

            if (id.equals("session-auth-fail")) isAuthenticated = false;
            if (id.equals("session-timeout-fail")) isTimedOut = true;
            if (id.equals("session-nav-fail")) isNavStateInvalid = true;

            Command cmd = new StartSessionCmd(id, "teller-1", "terminal-1", isAuthenticated, isTimedOut, isNavStateInvalid);
            aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(aggregate.uncommittedEvents());
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty());
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
