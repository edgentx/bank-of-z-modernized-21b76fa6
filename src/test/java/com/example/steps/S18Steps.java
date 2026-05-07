package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("test-session-id");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Configured in the command execution step below
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Configured in the command execution step below
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Default valid command for the happy path
            if (command == null) {
                command = new StartSessionCmd("teller-123", "term-456", true);
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // Rejection Scenarios
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("unauth-session");
        command = new StartSessionCmd("teller-bad", "term-bad", false); // isAuthenticated = false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("timeout-session");
        // Assuming a constructor or method to set last activity to a long time ago
        // For this exercise, we might rely on a command flag or internal state check
        // We will simulate this by passing a flag or setting state on the aggregate if supported.
        // Using the boolean flag in StartSessionCmd to simulate 'force timeout' check in domain logic for BDD simplicity
        command = new StartSessionCmd("teller-to", "term-to", false, true); // isAuthenticated=false, isTimedOut=true
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("nav-error-session");
        // Similar to timeout, using a flag to trigger the domain rule violation
        command = new StartSessionCmd("teller-nav", "term-nav", false, false, true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
