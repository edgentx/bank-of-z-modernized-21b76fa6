package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd.StartSessionCmdBuilder cmdBuilder;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to build a valid command baseline
    private StartSessionCmd.StartSessionCmdBuilder baseCommand() {
        return StartSessionCmd.builder() // Using the manual builder pattern below as records don't have builders by default, we will adapt to simple constructor in steps
                .sessionId("sess-123")
                .tellerId("teller-01")
                .terminalId("term-42")
                .isAuthenticated(true)
                .inactivityTimeoutMillis(900000) // 15 mins
                .operationalContext("MAIN_MENU");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("sess-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // We store default valid values in the When step or context, 
        // here we assume the command builder is primed with valid data.
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        the_start_session_cmd_command_is_executed_with(
                "teller-01", "term-42", true, 900000, "MAIN_MENU"
        );
    }

    private void the_start_session_cmd_command_is_executed_with(
            String tellerId, String terminalId, boolean authenticated, long timeout, String context
    ) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                    "sess-123",
                    tellerId,
                    terminalId,
                    authenticated,
                    timeout,
                    context
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events but got null (likely exception thrown)");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertEquals("session.started", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-123");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-123");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("sess-123");
    }

    // Refining When for negative cases using contextual assumptions derived from Gherkin description
    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_auth_violation() {
        // Scenario: Auth violation
        the_start_session_cmd_command_is_executed_with("teller-01", "term-42", false, 900000, "MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_timeout_violation() {
        // Scenario: Timeout violation (e.g. 0 or negative timeout)
        the_start_session_cmd_command_is_executed_with("teller-01", "term-42", true, 0, "MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_nav_violation() {
        // Scenario: Nav state violation (e.g. null/blank context)
        the_start_session_cmd_command_is_executed_with("teller-01", "term-42", true, 900000, "");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for RuntimeException or specific state exceptions
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}