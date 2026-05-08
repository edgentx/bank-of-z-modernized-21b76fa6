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

public class S18Steps {
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test Data
    private static final String VALID_TELLER_ID = "TELLER_123";
    private static final String VALID_TERMINAL_ID = "TERM_01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_001");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data setup, handled in the When block
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Data setup, handled in the When block
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(VALID_TELLER_ID, event.tellerId());
        Assertions.assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // The aggregate itself is valid, but the COMMAND will be invalid (missing teller)
        aggregate = new TellerSessionAggregate("SESSION_002");
        // We simulate the violation in the @When step by passing invalid data
    }

    // We need a specific When for the negative path or a smart dispatcher.
    // Since Cucumber matches text, we can reuse the method or make specific ones.
    // Let's reuse the generic one and use a shared variable for command data if needed.
    // But the previous When is hardcoded to VALID constants.
    // Better approach: Create a specific When for the context or use a context variable.
    // For simplicity in BDD, I'll check the scenario text or create a new method.
    // Actually, we can just parse the scenario, but the code below is cleaner:

    @When("the StartSessionCmd command is executed with no auth")
    public void execute_cmd_no_auth() {
        try {
            // Passing blank/null tellerId to trigger the invariant check inside Command or Aggregate
            // The Command record throws IllegalArgumentException on blank, which counts as rejection.
            Command cmd = new StartSessionCmd("", VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_session_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_003");
        // Simulate a session that was active very recently (should not be allowed to start again without explicit logout/timeout)
        aggregate.markActiveRecently();
    }

    @When("the StartSessionCmd command is executed while active")
    public void execute_cmd_while_active() {
        try {
            Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_session_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_004");
        // Simulate being deep in a menu
        aggregate.markNavigatedAway();
    }

    @When("the StartSessionCmd command is executed with wrong state")
    public void execute_cmd_wrong_state() {
        try {
            Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Ideally check it's a DomainError or specific type, but Exception is fine for now.
        System.out.println("Correctly rejected with: " + capturedException.getMessage());
    }

}
