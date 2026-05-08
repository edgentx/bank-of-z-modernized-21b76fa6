package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermachine.model.SessionStartedEvent;
import com.example.domain.tellermachine.model.StartSessionCmd;
import com.example.domain.tellermachine.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("TS-01");
        // Ensure initial state is clean (no prior session)
        // This implicitly makes it valid for the happy path provided we give a valid command.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in the 'When' step via the command payload
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in the 'When' step via the command payload
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate("TS-AUTH-FAIL");
        // In a real scenario, we might mark the teller as 'blocked' or unauthenticated.
        // Here, we rely on the Command payload 'isAuthenticated' flag being false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("TS-TIMEOUT-FAIL");
        // Simulate a scenario where the previous session hasn't timed out yet or state is stale.
        // We will simulate this by trying to start a session on an aggregate that thinks it's active.
        // NOTE: Since this is a Start command on a fresh aggregate, this specific Gherkin Given is slightly
        // abstract. We will interpret this as testing the timeout config validation logic.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate("TS-NAV-FAIL");
        // Similar to timeout, we will trigger the validation logic for bad context via the command.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default happy path data
        String id = "U-123";
        String tid = "T-99";
        boolean auth = true;
        int timeout = 600;
        String context = "MAIN_MENU";

        // Modify data if we are in a specific failure scenario
        // (Simple heuristic based on ID for brevity in this step definition)
        if (aggregate.id().contains("AUTH")) {
            auth = false;
        }
        if (aggregate.id().contains("TIMEOUT")) {
            timeout = -1; // Invalid configuration
        }
        if (aggregate.id().contains("NAV")) {
            context = null; // Invalid context
        }

        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), id, tid, auth, timeout, context);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("TS-01", event.aggregateId());
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
