package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a fresh aggregate that has not been started yet, with valid state
        aggregate = new TellerSessionAggregate("SESSION-1");
        aggregate.hydrateForTest(true, 0L, "HOME");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Implicitly handled in the When step via new StartSessionCmd("TELLER-1", ...)
        // No state change needed here, the step ensures context for the command creation
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Implicitly handled in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Assuming valid parameters for the positive flow
            StartSessionCmd cmd = new StartSessionCmd("SESSION-1", "TELLER-1", "TERM-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.started", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        aggregate.hydrateForTest(false, 0L, "HOME"); // isAuthenticated = false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        // Simulate a session that is old. 10 hours ago > 30 min timeout.
        long oldTimestamp = System.currentTimeMillis() - (10 * 60 * 60 * 1000); 
        aggregate.hydrateForTest(true, oldTimestamp, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // State is not HOME, implies session is already active/busy
        aggregate.hydrateForTest(true, 0L, "TRANSACTION_SCREEN");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // In the domain model we throw IllegalStateException for invariants
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
